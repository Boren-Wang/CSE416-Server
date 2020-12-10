import pstats

if __name__ == '__main__':
    s = pstats.Stats('profile1.out')
    s.sort_stats('tottime')
    s.print_stats()