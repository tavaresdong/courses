"""Coding practice for Lab 1."""

# While Loops

def factors(n):
    """Prints out all of the numbers that divide `n` evenly.

    >>> factors(20)
    20
    10
    5
    4
    2
    1
    """
    orig = n
    while orig >= 1:
        if n % orig == 0:
            print (orig)
        orig = orig - 1



def falling(n, k):
    """Compute the falling factorial of n to depth k.

    >>> falling(6, 3)  # 6 * 5 * 4
    120
    >>> falling(4, 0)
    1
    >>> falling(4, 3)  # 4 * 3 * 2
    24
    >>> falling(4, 1)  # 4
    4
    """
    product = 1
    while k > 0:
        product = product * n
        n = n - 1
        k = k - 1
    return product

