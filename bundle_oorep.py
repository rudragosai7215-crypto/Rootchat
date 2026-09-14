#!/usr/bin/env python3
"""
Bundle OOREP Boericke Materia Medica into app/src/main/assets/database/rootchart_clinical.db
"""

import gzip
import sqlite3
import os
import sys

DB_PATH = "app/src/main/assets/database/rootchart_clinical.db"
OOREP_PATH = "oorep.sql.gz"

def unescape_pg(text):
    if not text or text == r"\N":
        return ""
    res = []
    i = 0
    n = len(text)
    while i < n:
        if text[i] == '\\' and i + 1 < n:
            c = text[i+1]
            if c == 'n':
                res.append('\n')
            elif c == 'r':
                res.append('\r')
            elif c == 't':
                res.append('\t')
            elif c == '\\':
                res.append('\\')
            elif c == '"':
                res.append('"')
            elif c == "'":
                res.append("'")
            else:
                res.append(c)
            i += 2
        else:
            res.append(text[i])
            i += 1
    return "".join(res)

def bundle():
    if not os.path.exists(OOREP_PATH):
        print(f"Error: {OOREP_PATH} not found!")
        return False
    
    if not os.path.exists(DB_PATH):
        print(f"Error: {DB_PATH} not found!")
        return False

    print(f"Reading {OOREP_PATH}...")
    boericke_chapters = {} # mmchapter_id -> (heading, remedy_id)
    oorep_remedies = {}    # remedy_id -> (abbrev, fullname)

    # First pass: collect mmchapter and remedy
    with gzip.open(OOREP_PATH, "rt", encoding="utf-8", errors="replace") as f:
        curr = None
        for line in f:
            if line.startswith("COPY public.mmchapter "):
                curr = "mmchapter"
                continue
            elif line.startswith("COPY public.remedy "):
                curr = "remedy"
                continue
            elif line.startswith("COPY public."):
                curr = None
                continue
            elif line.startswith(r"\."):
                curr = None
                continue

            if curr == "mmchapter":
                parts = line.strip().split("\t")
                # (id, mminfo_id, heading, remedy_id)
                if len(parts) >= 4 and parts[1] == "20":
                    boericke_chapters[parts[0]] = (parts[2].strip(), parts[3].strip())
            elif curr == "remedy":
                parts = line.strip().split("\t")
                # (id, nameabbrev, namelong, namealt)
                if len(parts) >= 3:
                    rid = parts[0].strip()
                    abbr = parts[1].rstrip(".").strip().lower()
                    fullname = parts[2].strip()
                    oorep_remedies[rid] = (abbr, fullname)

    print(f"Found {len(boericke_chapters)} Boericke chapters and {len(oorep_remedies)} remedies.")

    # Second pass: collect mmsection for Boericke
    sections_to_insert = []
    with gzip.open(OOREP_PATH, "rt", encoding="utf-8", errors="replace") as f:
        curr = None
        for line in f:
            if line.startswith("COPY public.mmsection "):
                curr = "mmsection"
                continue
            elif curr == "mmsection":
                if line.startswith(r"\."):
                    break
                parts = line.strip().split("\t")
                # (id, mmchapter_id, depth, parent_sec_id, succ_sec_id, heading, content)
                if len(parts) >= 7:
                    cid = parts[1].strip()
                    if cid in boericke_chapters:
                        sec_heading = unescape_pg(parts[5].strip())
                        sec_content = unescape_pg(parts[6].strip())
                        if not sec_heading:
                            sec_heading = "General"
                        rid = boericke_chapters[cid][1]
                        if rid in oorep_remedies:
                            abbr, full = oorep_remedies[rid]
                        else:
                            chap_name = boericke_chapters[cid][0]
                            abbr = chap_name.lower().replace(" ", "-")
                            full = chap_name
                        
                        if sec_content:
                            sections_to_insert.append((abbr, full, sec_heading, sec_content))

    print(f"Total sections extracted: {len(sections_to_insert)}")

    # Connect to SQLite DB
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Recreate or clear materia_medica table
    cursor.execute("DROP TABLE IF EXISTS materia_medica")
    cursor.execute("""
        CREATE TABLE materia_medica (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            remedyAbbreviation TEXT NOT NULL,
            remedyFullName TEXT NOT NULL,
            section TEXT NOT NULL,
            symptomText TEXT NOT NULL
        )
    """)
    cursor.execute("CREATE INDEX IF NOT EXISTS index_materia_medica_remedyAbbreviation ON materia_medica (remedyAbbreviation)")
    cursor.execute("CREATE INDEX IF NOT EXISTS index_materia_medica_section ON materia_medica (section)")

    # Insert batch
    cursor.executemany(
        "INSERT INTO materia_medica (remedyAbbreviation, remedyFullName, section, symptomText) VALUES (?, ?, ?, ?)",
        sections_to_insert
    )
    conn.commit()

    cursor.execute("SELECT COUNT(DISTINCT remedyAbbreviation), COUNT(*) FROM materia_medica")
    rem_count, total_count = cursor.fetchone()
    print(f"Successfully bundled into {DB_PATH}:")
    print(f"  Distinct Remedies: {rem_count}")
    print(f"  Total Sections: {total_count}")

    # Vacuum and optimize DB
    cursor.execute("VACUUM")
    conn.commit()
    conn.close()

    db_size = os.path.getsize(DB_PATH)
    print(f"New database size: {db_size / (1024*1024):.2f} MB")
    return True

if __name__ == "__main__":
    bundle()
