#!/usr/bin/env python3

import sys

from itertools import groupby
from operator import itemgetter


def main(separator='\t'):
    total_chunk_size, total_mean, total_variance = 0, 0, 0

    for line in sys.stdin:
        chunk_size, mean, variance = line.strip().split(separator)

        chunk_size = int(chunk_size)
        mean = float(mean)
        variance = float(variance)
    
        total_variance = (total_chunk_size * total_variance + chunk_size * variance) / (total_chunk_size + chunk_size) + total_chunk_size * chunk_size * ((total_mean - mean) / (total_chunk_size + chunk_size)) ** 2
        total_mean = (total_chunk_size * total_mean + chunk_size * mean) / (total_chunk_size + chunk_size)
        total_chunk_size += chunk_size

    print(total_chunk_size, total_mean, total_variance, sep=separator)


if __name__ == "__main__":
    main()