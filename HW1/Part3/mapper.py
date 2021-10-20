#!/usr/bin/env python3

import sys
import csv

from typing import List


CHUNK_SIZE = 10


def read_input(file) -> List[float]:
    reader = csv.reader(file, delimiter=',')
    next(reader, None)

    i = 1
    values = []
    for value in reader:
        try:
            if i < CHUNK_SIZE:
                values.append(float(value[0]))
                i += 1
            else:
                yield values + [float(value[0])]
                i = 1
                values.clear()
        except ValueError:
            pass
            
    yield values


def get_mean(values: List[float]) -> float:
    return sum(values) / len(values)


def get_variance(values: List[float]) -> float:
    mean = get_mean(values)
    return sum((x - mean) ** 2 for x in values) / len(values)


def main(separator='\t'):
    data = read_input(sys.stdin)
    for values in data:
        if len(values):
            print(len(values), get_mean(values), get_variance(values), sep=separator)


if __name__ == "__main__":
    main()
