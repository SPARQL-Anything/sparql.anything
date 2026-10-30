# Regenerates the Parquet test fixtures in ../resources.
# Requires pyarrow: pip install pyarrow && python generate_fixtures.py
import datetime
import decimal
import os
import uuid

import pyarrow as pa
import pyarrow.parquet as pq

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "resources")


def write(table, name, **kwargs):
    pq.write_table(table, os.path.join(OUT, name + ".parquet"), **kwargs)


# Flat file with one column per supported type; row 2 holds nulls in the nullable columns
flat = pa.table({
    "string": pa.array(["Ghent", None], pa.string()),
    "boolean": pa.array([True, False], pa.bool_()),
    "int8": pa.array([-8, 8], pa.int8()),
    "int16": pa.array([-16, 16], pa.int16()),
    "int32": pa.array([-32, None], pa.int32()),
    "int64": pa.array([-64, 64], pa.int64()),
    "uint8": pa.array([255, 1], pa.uint8()),
    "uint16": pa.array([65535, 1], pa.uint16()),
    "uint32": pa.array([4294967295, 1], pa.uint32()),
    "uint64": pa.array([18446744073709551615, 1], pa.uint64()),
    "float": pa.array([1.5, -0.25], pa.float32()),
    "double": pa.array([2.5, 1e-10], pa.float64()),
    "decimal": pa.array([decimal.Decimal("12.50"), decimal.Decimal("-0.01")], pa.decimal128(10, 2)),
    "date": pa.array([datetime.date(2026, 10, 30), datetime.date(1970, 1, 1)], pa.date32()),
    "time": pa.array([datetime.time(10, 15, 30, 123000), datetime.time(0, 0)], pa.time64("us")),
    "timestamp_utc": pa.array([datetime.datetime(2026, 10, 30, 11, 12, 2, 500000), datetime.datetime(2000, 1, 1)], pa.timestamp("ms", tz="UTC")),
    "timestamp_local": pa.array([datetime.datetime(2026, 10, 30, 11, 12, 2), datetime.datetime(2000, 1, 1)], pa.timestamp("us")),
    "binary": pa.array([b"\x00\x01\x02", b"hi"], pa.binary()),
    "uuid": pa.array([uuid.UUID("12345678-1234-5678-1234-567812345678").bytes, uuid.UUID(int=1).bytes], pa.uuid()),
}, metadata={"origin": "sparql-anything test"})
write(flat, "Flat", compression="snappy")

# Nested types
nested = pa.table({
    "id": pa.array([1, 2, 3], pa.int32()),
    "tags": pa.array([["a", "b"], [], None], pa.list_(pa.string())),
    "point": pa.array([{"x": 1.0, "y": 2.0}, None, {"x": 3.0, "y": None}], pa.struct([("x", pa.float64()), ("y", pa.float64())])),
    "people": pa.array([[{"name": "Ann", "age": 30}], [], [{"name": "Bob", "age": None}, {"name": "Cy", "age": 4}]],
                       pa.list_(pa.struct([("name", pa.string()), ("age", pa.int32())]))),
    "labels": pa.array([[("en", "house"), ("nl", "huis")], [], None], pa.map_(pa.string(), pa.string())),
    "codes": pa.array([[(1, "one")], [(2, "two"), (3, "three")], []], pa.map_(pa.int32(), pa.string())),
    "matrix": pa.array([[[1, 2], [3]], [], [[]]], pa.list_(pa.list_(pa.int64()))),
})
write(nested, "Nested")

# The same small table with every codec the triplifier supports, plus Brotli which it does not
codecs = pa.table({"name": ["Ann", "Bob"], "value": pa.array([1, 2], pa.int64())})
for codec in ["none", "gzip", "snappy", "zstd", "brotli"]:
    write(codecs, "codec_" + codec, compression=codec)

# Example file for the documentation (to be published as https://sparql-anything.cc/examples/simple.parquet)
write(codecs, "simple", compression="snappy")

# Column names that need sanitizing
write(pa.table({"first name": ["Ann"], "e-mail (work)": ["ann@example.org"]}), "Headers")

# Several row groups
write(pa.table({"n": pa.array(range(1, 8), pa.int32())}), "RowGroups", row_group_size=3)

# Schema but no rows
write(pa.table({"name": pa.array([], pa.string()), "value": pa.array([], pa.int64())}), "Empty")
