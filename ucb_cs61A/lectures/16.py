def str_repr_demos():
    from fractions import Fraction
    half = Fraction(1, 2)
    half
    print(half)
    str(half)
    repr(half)

    s = 'hello world'
    str(s)
    repr(s)
    "'hello world'"
    repr(repr(repr(s)))
    eval(eval(eval(repr(repr(repr(s))))))
    # Errors: eval('hello world')

# Implementing generic string functions

class Bear:
    """A Bear."""
    def __init__(self):
        self.__repr__ = lambda: 'oski'
        self.__str__ = lambda: 'oski the bear'

    def __repr__(self):
        return 'Bear()'

    def __str__(self):
        return 'a bear'

def print_bear():
    oski = Bear()
    print(oski)
    print(str(oski))
    print(repr(oski))
    print(oski.__repr__())
    print(oski.__str__())

def repr(x):
    return type(x).__repr__(x)

def str(x):
    t = type(x)
    if hasattr(t, '__str__'):
        return t.__str__(x)
    else:
        return repr(x)

print_bear()

# Ratio numbers

class Ratio:
    """A mutable ratio.

    >>> f = Ratio(9, 15)
    >>> f
    Ratio(9, 15)
    >>> print(f)
    9/15
    >>> f.gcd
    3
    >>> f.numer = 6
    >>> f.denom
    10
    >>> f.gcd
    2
    >>> f
    Ratio(6, 10)
    >>> f.denom = 5
    >>> f
    Ratio(3, 5)

    >>> Ratio(1, 3) + Ratio(1, 6)
    Ratio(1, 2)
    >>> f + 1
    Ratio(8, 5)
    >>> 1 + f
    Ratio(8, 5)
    >>> 1.4 + f
    2.0
    """
    def __init__(self, n, d):
        self.gcd = gcd(n, d)
        self._numer = n // self.gcd
        self._denom = d // self.gcd

    def __repr__(self):
        return 'Ratio({0}, {1})'.format(self.numer, self.denom)

    def __str__(self):
        return '{0}/{1}'.format(self.numer, self.denom)

    def __add__(self, other):
        if isinstance(other, Ratio):
            n = self.numer * other.denom + self.denom * other.numer
            d = self.denom * other.denom
        elif isinstance(other, int):
            n = self.numer + self.denom * other
            d = self.denom
        else:
            return float(self) + other
        r = Ratio(n, d)
        r.gcd = 1
        return r

    __radd__ = __add__

    def __float__(self):
        return self.numer / self.denom

    @property
    def numer(self):
        return self._numer * self.gcd

    @property
    def denom(self):
        return self._denom * self.gcd

    @numer.setter
    def numer(self, value):
        assert value % self._numer == 0
        self.gcd = value // self._numer       

    @denom.setter
    def denom(self, value):
        assert value % self._denom == 0
        self.gcd = value // self._denom       


def gcd(x, y):
    """Return the greatest common divisor of integers x & y.
    
    >>> gcd(12, 8)
    4
    """
    while x != y:
        x, y = abs(x-y), min(x, y)
    return x
