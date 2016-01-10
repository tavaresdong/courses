def g(n):
    """Return the value of G(n), computed recursively.

    >>> g(1)
    1
    >>> g(2)
    2
    >>> g(3)
    3
    >>> g(4)
    10
    >>> g(5)
    22
    """
    "*** YOUR CODE HERE ***"
    if n <= 3:
        return n
    else:
        return g(n - 1) + 2 * g(n - 2) + 3 * g(n - 3)

def g_iter(n):
    """Return the value of G(n), computed iteratively.

    >>> g_iter(1)
    1
    >>> g_iter(2)
    2
    >>> g_iter(3)
    3
    >>> g_iter(4)
    10
    >>> g_iter(5)
    22
    """
    "*** YOUR CODE HERE ***"
    g1, g2, g3, cur, ind = 1, 2, 3, 0, 3
    if n < 3:
        return n
    else:
        while ind < n:
            cur = g3 + 2 * g2 + 3 * g1
            ind += 1
            g1, g2, g3 = g2, g3, cur
        return g3




def pingpong(n):
    """Return the nth element of the ping-pong sequence.

    >>> pingpong(7)
    7
    >>> pingpong(8)
    6
    >>> pingpong(15)
    1
    >>> pingpong(21)
    -1
    >>> pingpong(22)
    0
    >>> pingpong(30)
    6
    >>> pingpong(68)
    2
    >>> pingpong(69)
    1
    >>> pingpong(70)
    0
    >>> pingpong(71)
    1
    >>> pingpong(72)
    0
    >>> pingpong(100)
    2
    """
    "*** YOUR CODE HERE ***"
    def change(ind):
        if ind % 7 == 0 or has_seven(ind):
            return True
        return False

    def pong(up, ind, val):
        if ind == n:
            return val
        if change(ind):
            if up == 0:
                return pong(1 - up, ind + 1, val - 1)
            else:
                return pong(1 - up, ind + 1, val + 1)
        else:
            if up == 0:
                return pong(up, ind + 1, val + 1)
            else:
                return pong(up, ind + 1, val - 1)
    return pong(0, 1, 1) 


def has_seven(k):
    """Returns True if at least one of the digits of k is a 7, False otherwise.

    >>> has_seven(3)
    False
    >>> has_seven(7)
    True
    >>> has_seven(2734)
    True
    >>> has_seven(2634)
    False
    >>> has_seven(734)
    True
    >>> has_seven(7777)
    True
    """
    if k % 10 == 7:
        return True
    elif k < 10:
        return False
    else:
        return has_seven(k // 10)


def count_change(amount):
    """Return the number of ways to make change for amount.

    >>> count_change(7)
    6
    >>> count_change(10)
    14
    >>> count_change(20)
    60
    >>> count_change(100)
    9828
    """
    def change_with_maxcoin(total, maxcoin):
        if total == 0:
            return 1
        if maxcoin == 0:
            return 0
        cnt = 0
        num_change = 0
        while cnt * maxcoin <= total:
            num_change += change_with_maxcoin(total - cnt * maxcoin, maxcoin // 2)
            cnt = cnt + 1
        return num_change

    maxcoin = 1
    while maxcoin < amount:
        maxcoin = maxcoin * 2
    if maxcoin > amount:
        maxcoin = maxcoin // 2

    return change_with_maxcoin(amount, maxcoin)




def print_move(origin, destination):
    """Print instructions to move a disk."""
    print("Move the top disk from rod", origin, "to rod", destination)

def move_stack(n, start, end):
    """Print the moves required to move n disks on the start pole to the end
    pole without violating the rules of Towers of Hanoi.

    n -- number of disks
    start -- a pole position, either 1, 2, or 3
    end -- a pole position, either 1, 2, or 3

    There are exactly three poles, and start and end must be different. Assume
    that the start pole has at least n disks of increasing size, and the end
    pole is either empty or has a top disk larger than the top n start disks.

    >>> move_stack(1, 1, 3)
    Move the top disk from rod 1 to rod 3
    >>> move_stack(2, 1, 3)
    Move the top disk from rod 1 to rod 2
    Move the top disk from rod 1 to rod 3
    Move the top disk from rod 2 to rod 3
    >>> move_stack(3, 1, 3)
    Move the top disk from rod 1 to rod 3
    Move the top disk from rod 1 to rod 2
    Move the top disk from rod 3 to rod 2
    Move the top disk from rod 1 to rod 3
    Move the top disk from rod 2 to rod 1
    Move the top disk from rod 2 to rod 3
    Move the top disk from rod 1 to rod 3
    """
    assert 1 <= start <= 3 and 1 <= end <= 3 and start != end, "Bad start/end"
    "*** YOUR CODE HERE ***"
    if n == 1:
        print_move(start, end)
        return
    handle = 6 - start - end
    move_stack(n - 1, start, handle)
    print_move(start, end)
    move_stack(n - 1, handle, end)
    return


from operator import sub, mul

def make_anonymous_factorial():
    """Return the value of an expression that computes factorial.

    >>> make_anonymous_factorial()(5)
    120
    """
    return lambda val : (lambda f, v : f(f, v)) (lambda f, v : 1 if v == 0 else mul(v, f(f, sub(v, 1))), val) 

