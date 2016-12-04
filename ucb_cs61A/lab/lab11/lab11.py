#############
# Iterators #
#############

# Q2
class IteratorRestart:
    """
    >>> iterator = IteratorRestart(2, 7)
    >>> for num in iterator:
    ...     print(num)
    2
    3
    4
    5
    6
    7
    >>> for num in iterator:
    ...     print(num)
    2
    3
    4
    5
    6
    7
    """
    def __init__(self, start, end):
        self.val = start
        self.start = start
        self.end = end

    def __next__(self):
        if self.val <= self.end:
            orig = self.val
            self.val += 1
            return orig
        else:
            self.val = self.start
            raise StopIteration

    def __iter__(self):
        return self


# Q3
class Str:
    """
    >>> s = Str("hello")
    >>> for char in s:
    ...     print(char)
    ...
    h
    e
    l
    l
    o
    >>> for char in s:    # a standard iterator does not restart
    ...     print(char)
    """
    def __init__(self, string):
        self.string = string
        self.pos = -1

    def __iter__(self):
        return self

    def __next__(self):
        if self.pos < len(self.string) - 1:
            self.pos += 1
            return self.string[self.pos]
        else:
            raise StopIteration

##############
# Generators #
##############

# Q4
def countdown(n):
    """
    >>> from types import GeneratorType
    >>> type(countdown(0)) is GeneratorType # countdown is a generator
    True
    >>> for number in countdown(5):
    ...     print(number)
    ...
    5
    4
    3
    2
    1
    0
    """
    "*** YOUR CODE HERE ***"
    while n >= 0:
        yield n
        n -= 1

class Countdown:
    """
    >>> from types import GeneratorType
    >>> type(Countdown(0)) is GeneratorType # Countdown is an iterator
    False
    >>> for number in Countdown(5):
    ...     print(number)
    ...
    5
    4
    3
    2
    1
    0
    """
    "*** YOUR CODE HERE ***"
    def __init__(self, n):
        self.n = n

    # yield makes this method a generator function
    def __iter__(self):
        while self.n >= 0:
            yield self.n
            self.n -= 1

# Q5
def hailstone(n):
    """
    >>> for num in hailstone(10):
    ...     print(num)
    ...
    10
    5
    16
    8
    4
    2
    1
    """
    while n != 1:
        yield n
        if n % 2 == 0:
            n = n // 2
        else:
            n = n * 3 + 1
    yield n


