#!/usr/bin/env python3
"""
OOREP Materia Medica Importer
Extracts Boericke's Materia Medica (1906, Public Domain) from OOREP database dump
and populates the materia_medica table in rootchart_clinical.db.
"""

import os
import sys
import gzip
import zipfile
import sqlite3
import re

DB_PATH = "app/src/main/assets/database/rootchart_clinical.db"

def find_dump_file():
    candidates = [
        "oorep.sql",
        "oorep.sql.gz",
        "oorep.zip",
        "oorep-0.18.0.zip",
        "oorep-0.18.0/oorep.sql.gz",
        "app/src/main/assets/oorep.sql",
        "app/src/main/assets/oorep.sql.gz",
        "app/src/main/assets/database/oorep.sql",
        "app/src/main/assets/database/oorep.sql.gz"
    ]
    for c in candidates:
        if os.path.exists(c):
            return c
    return None

def open_dump_stream(path):
    if path.endswith(".zip"):
        z = zipfile.ZipFile(path, 'r')
        for name in z.namelist():
            if name.endswith(".sql") or name.endswith(".sql.gz"):
                f = z.open(name)
                if name.endswith(".gz"):
                    return gzip.GzipFile(fileobj=f)
                return f
        raise ValueError("No .sql or .sql.gz found in zip")
    elif path.endswith(".gz"):
        return gzip.open(path, 'rt', encoding='utf-8', errors='replace')
    else:
        return open(path, 'r', encoding='utf-8', errors='replace')

def unescape_pg(text):
    if not text or text == r'\N':
        return ""
    return (text.replace(r'\n', '\n')
                .replace(r'\r', '\r')
                .replace(r'\t', '\t')
                .replace(r'\\', '\\')
                .replace(r'\"', '"')
                .replace(r"\'", "'"))

def run_import(dump_path=None):
    if not dump_path:
        dump_path = find_dump_file()
    if not dump_path:
        print("No OOREP dump file found in workspace.")
        return False

    print(f"Opening OOREP dump: {dump_path}")
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Preload remedy names
    cursor.execute("SELECT abbreviation, fullName FROM remedy_names")
    name_to_abbr = {}
    abbr_to_name = {}
    for abbr, full_name in cursor.fetchall():
        clean = full_name.lower().replace('.', '').replace('-', ' ').strip()
        name_to_abbr[clean] = abbr
        abbr_to_name[abbr.lower()] = full_name

    # Ensure table exists
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS materia_medica (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            remedyAbbreviation TEXT NOT NULL,
            remedyFullName TEXT NOT NULL,
            section TEXT NOT NULL,
            symptomText TEXT NOT NULL
        )
    """)
    cursor.execute("CREATE INDEX IF NOT EXISTS index_materia_medica_remedyAbbreviation ON materia_medica (remedyAbbreviation)")
    cursor.execute("CREATE INDEX IF NOT EXISTS index_materia_medica_section ON materia_medica (section)")

    stream = open_dump_stream(dump_path)
    
    current_table = None
    copy_columns = []
    boericke_info_id = None
    chapters = {} # id -> (fullName, abbrev)
    sections_to_insert = []
    total_sections = 0

    for line in stream:
        if isinstance(line, bytes):
            line = line.decode('utf-8', errors='replace')
        
        trimmed = line.strip()
        if trimmed.startswith("COPY ") or trimmed.startswith("copy "):
            m = re.search(r"COPY\s+(?:public\.)?([a-zA-Z0-9_]+)\s*(?:\(([^)]+)\))?", trimmed, re.I)
            if m:
                current_table = m.group(1).lower()
                cols_raw = m.group(2)
                copy_columns = [c.strip().lower() for c in cols_raw.split(",")] if cols_raw else []
            continue

        if trimmed == r"\.":
            current_table = None
            copy_columns = []
            continue

        if current_table == "mminfo":
            parts = line.split('\t')
            id_idx = copy_columns.index("id") if "id" in copy_columns else 0
            abbrev_idx = copy_columns.index("abbrev") if "abbrev" in copy_columns else 2
            
            mid = parts[id_idx].strip() if len(parts) > id_idx else None
            abbrev = parts[abbrev_idx].strip() if len(parts) > abbrev_idx else None
            if abbrev and abbrev.lower() == "boericke":
                boericke_info_id = mid
                print(f"Found Boericke MM Info ID: {boericke_info_id}")

        elif current_table == "mmchapter":
            parts = line.split('\t')
            id_idx = copy_columns.index("id") if "id" in copy_columns else 0
            info_idx = copy_columns.index("mminfoid") if "mminfoid" in copy_columns else 1
            name_idx = copy_columns.index("name") if "name" in copy_columns else 2

            cid = parts[id_idx].strip() if len(parts) > id_idx else None
            infoid = parts[info_idx].strip() if len(parts) > info_idx else None
            raw_name = parts[name_idx].strip() if len(parts) > name_idx else None

            if cid and (infoid == boericke_info_id or (boericke_info_id is None and infoid == "1")):
                decoded = unescape_pg(raw_name)
                norm = decoded.lower().replace('.', '').replace('-', ' ').strip()
                abbr = name_to_abbr.get(norm, decoded.lower().replace(' ', '-'))
                full = abbr_to_name.get(abbr.lower(), decoded)
                chapters[cid] = (full, abbr)

        elif current_table == "mmsection":
            parts = line.split('\t')
            cid_idx = copy_columns.index("mmchapterid") if "mmchapterid" in copy_columns else 1
            name_idx = copy_columns.index("name") if "name" in copy_columns else 2
            text_idx = copy_columns.index("text") if "text" in copy_columns else 3

            cid = parts[cid_idx].strip() if len(parts) > cid_idx else None
            sec_name_raw = parts[name_idx].strip() if len(parts) > name_idx else "General"
            text_raw = parts[text_idx].strip() if len(parts) > text_idx else ""

            if cid in chapters:
                full, abbr = chapters[cid]
                sec_name = unescape_pg(sec_name_raw)
                text = unescape_pg(text_raw)
                if text:
                    sections_to_insert.append((abbr, full, sec_name, text))
                    if len(sections_to_insert) >= 1000:
                        cursor.executemany(
                            "INSERT INTO materia_medica (remedyAbbreviation, remedyFullName, section, symptomText) VALUES (?, ?, ?, ?)",
                            sections_to_insert
                        )
                        total_sections += len(sections_to_insert)
                        sections_to_insert = []

    if sections_to_insert:
        cursor.executemany(
            "INSERT INTO materia_medica (remedyAbbreviation, remedyFullName, section, symptomText) VALUES (?, ?, ?, ?)",
            sections_to_insert
        )
        total_sections += len(sections_to_insert)

    conn.commit()
    cursor.execute("SELECT COUNT(DISTINCT remedyAbbreviation), COUNT(*) FROM materia_medica")
    remedy_count, total_count = cursor.fetchone()
    conn.close()

    print(f"Import Complete!")
    print(f"Remedies imported: {remedy_count}")
    print(f"Total sections imported: {total_count}")
    return True

if __name__ == "__main__":
    path = sys.argv[1] if len(sys.argv) > 1 else None
    run_import(path)
