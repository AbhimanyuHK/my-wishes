#!/usr/bin/env python3
"""Validate bundled English/Kannada greeting catalog shape and stable IDs."""

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CATALOG_DIR = ROOT / "app" / "src" / "main" / "assets" / "catalog"
CATALOGS = {
    "en": CATALOG_DIR / "occasions_en.json",
    "kn": CATALOG_DIR / "occasions_kn.json",
}


def load_catalog(language: str) -> dict:
    path = CATALOGS[language]
    with path.open(encoding="utf-8") as stream:
        catalog = json.load(stream)
    assert catalog.get("schemaVersion") == 1, f"{path}: unsupported schemaVersion"
    assert catalog.get("language") == language, f"{path}: language code mismatch"
    occasions = catalog.get("occasions")
    assert isinstance(occasions, list) and occasions, f"{path}: no occasions"

    occasion_ids = []
    greeting_ids = []
    for occasion in occasions:
        assert occasion.get("id", "").strip(), f"{path}: occasion missing ID"
        assert occasion.get("title", "").strip(), f"{path}: occasion missing title"
        assert isinstance(occasion.get("greetings"), list) and occasion["greetings"], (
            f"{path}: {occasion.get('id')} has no greetings"
        )
        occasion_ids.append(occasion["id"])
        for greeting in occasion["greetings"]:
            assert greeting.get("id", "").strip(), f"{path}: greeting missing ID"
            assert greeting.get("text", "").strip(), (
                f"{path}: {greeting.get('id')} has empty text"
            )
            greeting_ids.append(greeting["id"])

    assert len(occasion_ids) == len(set(occasion_ids)), f"{path}: duplicate occasion IDs"
    assert len(greeting_ids) == len(set(greeting_ids)), f"{path}: duplicate greeting IDs"
    return catalog


def main() -> int:
    catalogs = {language: load_catalog(language) for language in CATALOGS}
    english = catalogs["en"]
    kannada = catalogs["kn"]

    english_occasions = {item["id"]: item for item in english["occasions"]}
    kannada_occasions = {item["id"]: item for item in kannada["occasions"]}
    assert english_occasions.keys() == kannada_occasions.keys(), (
        "English and Kannada occasion IDs do not match"
    )

    for occasion_id, en_occasion in english_occasions.items():
        kn_occasion = kannada_occasions[occasion_id]
        en_ids = {greeting["id"] for greeting in en_occasion["greetings"]}
        kn_ids = {greeting["id"] for greeting in kn_occasion["greetings"]}
        assert en_ids == kn_ids, f"Greeting ID mismatch for occasion {occasion_id}"

    print(
        "Catalog validation passed: "
        f"{len(english_occasions)} shared occasions, "
        f"{sum(len(o['greetings']) for o in english['occasions'])} greetings per language."
    )
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (AssertionError, OSError, json.JSONDecodeError) as error:
        print(f"Catalog validation failed: {error}", file=sys.stderr)
        sys.exit(1)
