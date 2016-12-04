test = {
  'name': 'num-leaves',
  'points': 0,
  'suites': [
    {
      'cases': [
        {
          'code': r"""
          scm> (num-leaves nil)
          0
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          scm> (num-leaves test-tree)
          2
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          scm> (num-leaves (right test-tree))
          1
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          scm> (num-leaves (left test-tree))
          1
          """,
          'hidden': False,
          'locked': False
        }
      ],
      'scored': False,
      'setup': r"""
      scm> (load 'lab09_extra)
      """,
      'teardown': '',
      'type': 'scheme'
    }
  ]
}