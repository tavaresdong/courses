from lab04 import *

# Q12
def flatten(lst):
    """Returns a flattened version of lst.

    >>> flatten([1, 2, 3])     # normal list
    [1, 2, 3]
    >>> x = [1, [2, 3], 4]      # deep list
    >>> flatten(x)
    [1, 2, 3, 4]
    >>> x = [[1, [1, 1]], 1, [1, 1]] # deep list
    >>> flatten(x)
    [1, 1, 1, 1, 1, 1]
    """
    "*** YOUR CODE HERE ***"
    if type(lst) != list:
        return [lst]
    else:
        flst = []
        for elem in lst:
            if type(elem) != list:
                flst.append(elem)
            else:
                flst.extend(flatten(elem))
        return flst


# Q13
def merge(lst1, lst2):
    """Merges two sorted lists.

    >>> merge([1, 3, 5], [2, 4, 6])
    [1, 2, 3, 4, 5, 6]
    >>> merge([], [2, 4, 6])
    [2, 4, 6]
    >>> merge([1, 2, 3], [])
    [1, 2, 3]
    >>> merge([5, 7], [2, 4, 6])
    [2, 4, 5, 6, 7]
    """
    "*** YOUR CODE HERE ***"
    assert type(lst1) == list and type(lst2) == list
    merge_lst = []
    if len(lst1) == 0:
        return lst2
    elif len(lst2) == 0:
        return lst1
    else:
        if lst1[0] < lst2[0]:
            return [lst1[0]] + merge(lst1[1:], lst2)
        else:
            return [lst2[0]] + merge(lst1, lst2[1:])


