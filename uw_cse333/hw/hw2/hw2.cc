#include <cstdio>
#include <cassert>
#include <iostream>
#include <functional>

#include "array2d.cc"
#include "jansson.h"

int decoder(json_t* json)
{
  if (!json_is_integer(json))
    return 0;
  return json_integer_value(json);
}

json_t* encoder(const int& val)
{
  return json_integer(val + 1);
}

int main(int argc, char** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: ./hw2 file_to_load" << std::endl;
    return -1;
  }

  FILE* f = fopen(argv[1], "r"); 
  assert(f != nullptr);

  json_t* json = json_loadf(f, 0, NULL);
  assert(json != nullptr);

  Array2d<int> array;

  // (1) Deserialize the array
  auto res = DeserializeArray2d(array, json, std::function<int(json_t*)>(decoder));
  assert(res == true);
  PrintArray2d(array);

  // (2) Set [1][1] to 100
  res = SetArray2d(array, 1, 1, 100);
  assert(res == true);
  PrintArray2d(array);

  // (3) Swap [1][1] and [0][0]
  res = SwapArray2d(array, 1, 1, 0, 0);
  assert(res == true);
  PrintArray2d(array);

  // (4) Try to swap [0][0] and [10][10], should fail
  res = SwapArray2d(array, 0, 0, 10, 10);
  assert(res == false);
  PrintArray2d(array);

  // (5) Serialize the Array2d to json
  json_t* serializedArray = SerializeArray2d(array, std::function<json_t* (const int&)>(encoder));
  assert(serializedArray != nullptr);
  std::cout << json_dumps(serializedArray, 0) << std::endl;

  return 0;
}
