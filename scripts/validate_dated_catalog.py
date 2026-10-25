#!/usr/bin/env python3
"""Validate bilingual offline greeting and dated-event assets."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "app/src/main/assets/catalog"


def read(name):
    return json.loads((ROOT / name).read_text(encoding="utf-8"))


def check_greetings(items, label):
    ids = [g["id"] for g in items]
    assert len(items) >= 5, f"{label}: fewer than five wishes"
    assert len(ids) == len(set(ids)), f"{label}: duplicate wish IDs"
    assert all(g["text"].strip() for g in items), f"{label}: empty text"


def validate():
    categories = {}
    dates = {}
    for lang in ("en", "kn"):
        catalog = read(f"occasions_{lang}.json")
        occasions = catalog["occasions"]
        categories[lang] = {o["id"] for o in occasions}
        assert len(categories[lang]) == len(occasions)
        for o in occasions:
            check_greetings(o["greetings"], o["id"])

        events = read(f"dated_events_{lang}.json")["events"]
        assert len(events) == 90, f"{lang}: expected 90 fixed-date event records"
        dates[lang] = {e["id"]: e["date"] for e in events}
        assert len(dates[lang]) == len(events), f"{lang}: duplicate event ID"
        from datetime import date
        for event in events:
            year = date.fromisoformat(event["date"]).year
            assert 2026 <= year <= 2030
            check_greetings(event["greetings"], event["id"])
        for year in range(2026, 2031):
            assert sum(e["date"].startswith(f"{year}-") for e in events) == 18
    assert categories["en"] == categories["kn"], "Category translations out of sync"
    assert dates["en"] == dates["kn"], "Event dates/translations out of sync"
    print("PASS: bilingual categories and 90 dated events per language; >=5 wishes each")


if __name__ == "__main__":
    validate()
