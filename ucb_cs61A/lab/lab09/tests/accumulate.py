test = {
  'name': 'accumulate',
  'points': 0,
  'suites': [
    {
      'cases': [
        {
          'code': r"""
          scm> (accumulate + 0 4 square)
          30
          """,
          'hidden': False,
          'locked': False
        }
      ],
      'scored': False,
      'setup': r"""
      scm> (load 'lab09_extra)
      scm> (define (square x) (* x x))
      """,
      'teardown': '',
      'type': 'scheme'
    },
    {
      'cases': [
        {
          'code': r"""
          scm> (accumulate * 3 3 identity)
          18
          """,
          'hidden': False,
          'locked': False
        }
      ],
      'scored': False,
      'setup': r"""
      scm> (load 'lab09_extra)
      scm> (define (identity x) x)
      """,
      'teardown': '',
      'type': 'scheme'
    },
    {
      'cases': [
        {
          'code': r"""
          scm> (accumulate + 1 5 add-one)
          21
          """,
          'hidden': False,
          'locked': False
        }
      ],
      'scored': False,
      'setup': r"""
      scm> (load 'lab09_extra)
      scm> (define (add-one x) (+ x 1))
      """,
      'teardown': '',
      'type': 'scheme'
    }
  ]
}