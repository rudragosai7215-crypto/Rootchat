#!/usr/bin/env python3
"""
Import script / seeder for Kent's Repertory from OOREP's public-domain 1897 SQL dump.
Source repository: https://github.com/nondeterministic/oorep
Table sources: rubric, remedy, rubricremedy (filtered to source_key / abbrev = "publicum").
Outputs a SQLite database compatible with Room Database (v2) containing all 74,000+ rubrics
across 2,400+ remedies, mapped directly to their classical grades.
"""

import os
import sys
import gzip
import sqlite3
import json
import urllib.request
import time

OOREP_SQL_GZ_URL = "https://raw.githubusercontent.com/nondeterministic/oorep/master/oorep.sql.gz"
CACHE_DIR = "/tmp/oorep"
SQL_GZ_FILE = os.path.join(CACHE_DIR, "oorep.sql.gz")
DEST_DIR = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "databases")
DEST_DB_FILE = os.path.join(DEST_DIR, "bhms_repertory_database.db")
ROOM_IDENTITY_HASH = "9c75a3403fa5dd199ae2ef0063249eca"

def download_dump():
    os.makedirs(CACHE_DIR, exist_ok=True)
    if not os.path.exists(SQL_GZ_FILE) or os.path.getsize(SQL_GZ_FILE) == 0:
        print(f"Downloading OOREP SQL dump from {OOREP_SQL_GZ_URL}...")
        urllib.request.urlretrieve(OOREP_SQL_GZ_URL, SQL_GZ_FILE)
        print("Download complete.")
    else:
        print(f"Using cached SQL dump at {SQL_GZ_FILE}.")

def parse_and_populate():
    start_time = time.time()
    os.makedirs(DEST_DIR, exist_ok=True)
    if os.path.exists(DEST_DB_FILE):
        os.remove(DEST_DB_FILE)

    print("Step 1: Reading remedies from dump...")
    remedies = {} # remedy_id (int) -> nameabbrev (str)
    with gzip.open(SQL_GZ_FILE, "rt", encoding="utf-8", errors="replace") as f:
        in_remedy = False
        for line in f:
            if line.startswith("COPY public.remedy "):
                in_remedy = True
                continue
            if in_remedy:
                if line.startswith("\\."):
                    break
                parts = line.strip().split("\t")
                if len(parts) >= 3:
                    rid = int(parts[0])
                    abbrev = parts[1].strip()
                    remedies[rid] = abbrev

    print(f"Loaded {len(remedies)} unique homeopathic remedies.")

    print("Step 2: Reading rubricremedy associations for publicum...")
    rubric_remedies = {} # rubric_id (int) -> dict of {remedy_abbrev: grade}
    with gzip.open(SQL_GZ_FILE, "rt", encoding="utf-8", errors="replace") as f:
        in_rr = False
        for line in f:
            if line.startswith("COPY public.rubricremedy "):
                in_rr = True
                continue
            if in_rr:
                if line.startswith("\\."):
                    break
                if line.startswith("publicum\t"):
                    parts = line.strip().split("\t")
                    # schema: abbrev, rubricid, remedyid, weight, chapterid
                    rubric_id = int(parts[1])
                    remedy_id = int(parts[2])
                    weight = int(parts[3])
                    rem_abbr = remedies.get(remedy_id)
                    if rem_abbr:
                        if rubric_id not in rubric_remedies:
                            rubric_remedies[rubric_id] = {}
                        rubric_remedies[rubric_id][rem_abbr] = weight

    print(f"Loaded remedy mappings for {len(rubric_remedies)} rubrics.")

    print("Step 3: Creating Room SQLite database and inserting rubrics...")
    conn = sqlite3.connect(DEST_DB_FILE)
    cur = conn.cursor()
    cur.execute("PRAGMA synchronous = OFF")
    cur.execute("PRAGMA journal_mode = MEMORY")

    # Room Schema matching AppDatabase version 2
    cur.execute("""
    CREATE TABLE IF NOT EXISTS `saved_cases` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        `doctorName` TEXT NOT NULL,
        `patientName` TEXT NOT NULL,
        `patientAge` INTEGER NOT NULL,
        `patientGender` TEXT NOT NULL,
        `chiefComplaint` TEXT NOT NULL,
        `caseType` TEXT NOT NULL,
        `selectedRubricsSummary` TEXT NOT NULL,
        `topRankedRemedies` TEXT NOT NULL,
        `prescribedRemedy` TEXT NOT NULL,
        `potency` TEXT NOT NULL,
        `dosage` TEXT NOT NULL,
        `followUpNotes` TEXT NOT NULL,
        `miasmSummary` TEXT NOT NULL,
        `createdAtTimestamp` INTEGER NOT NULL
    )
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS `kent_rubrics` (
        `id` INTEGER NOT NULL,
        `chapter` TEXT NOT NULL,
        `rubricText` TEXT NOT NULL,
        `remediesJson` TEXT NOT NULL,
        PRIMARY KEY(`id`)
    )
    """)

    cur.execute("CREATE INDEX IF NOT EXISTS `index_kent_rubrics_chapter` ON `kent_rubrics` (`chapter`)")
    cur.execute("CREATE INDEX IF NOT EXISTS `index_kent_rubrics_rubricText` ON `kent_rubrics` (`rubricText`)")

    cur.execute("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)")
    cur.execute(f"INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, '{ROOM_IDENTITY_HASH}')")

    rubric_rows = []
    with gzip.open(SQL_GZ_FILE, "rt", encoding="utf-8", errors="replace") as f:
        in_rubric = False
        for line in f:
            if line.startswith("COPY public.rubric "):
                in_rubric = True
                continue
            if in_rubric:
                if line.startswith("\\."):
                    break
                if line.startswith("publicum\t"):
                    parts = line.strip().split("\t")
                    rubric_id = int(parts[1])
                    fullpath = parts[5].strip()

                    # Extract Chapter and Rubric Text from fullpath
                    if "," in fullpath:
                        first_part = fullpath.split(",")[0].strip()
                        chapter = first_part
                        rubric_text = fullpath[len(first_part)+1:].strip()
                    else:
                        chapter = "Generalities"
                        rubric_text = fullpath

                    remedy_map = rubric_remedies.get(rubric_id, {})
                    remedies_json = json.dumps(remedy_map, separators=(",", ":"))
                    rubric_rows.append((rubric_id, chapter, rubric_text, remedies_json))

    cur.executemany("INSERT INTO kent_rubrics (id, chapter, rubricText, remediesJson) VALUES (?, ?, ?, ?)", rubric_rows)
    conn.commit()
    conn.close()

    file_size_mb = os.path.getsize(DEST_DB_FILE) / (1024 * 1024)
    print(f"Successfully populated Room Database: {len(rubric_rows)} rubrics across {len(remedies)} remedies in {time.time() - start_time:.2f}s.")
    print(f"Database saved to {DEST_DB_FILE} ({file_size_mb:.2f} MB).")

if __name__ == "__main__":
    download_dump()
    parse_and_populate()
