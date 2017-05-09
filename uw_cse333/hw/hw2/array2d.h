#ifndef ARRAY2d_H
#define ARRAY2d_H

#include <vector>
#include <iostream>
#include <functional>

#include "jansson.h"

template <typename Type>
using Array2d = std::vector<std::vector<Type> >;

template <typename Type>
using DecodeFunc = std::function<Type (json_t*)>;

template <typename Type>
using EncodeFunc = std::function<json_t* (const Type&)>;

template <typename Type>
bool DeserializeArray2d(Array2d<Type>& array, json_t* json, DecodeFunc<Type> decoder)
{
  Array2d<Type> tmpArray;

  if (!json_is_object(json)) return false;

  json_t* rowsjson = json_object_get(json, "rows");
  json_t* colsjson = json_object_get(json, "columns");
  json_t* datajson = json_object_get(json, "data");
  if (rowsjson == NULL) return false;
  if (colsjson == NULL) return false;
  if (datajson == NULL) return false;
  
  size_t rows = json_integer_value(rowsjson);
  size_t cols = json_integer_value(colsjson);
  size_t arraySize = json_array_size(datajson);
  if (rows * cols != arraySize) return false;

  tmpArray.resize(rows);
  for (auto i = 0u; i < arraySize; ++i)
  {
    json_t* elemjson = json_array_get(datajson, i);
    Type val = decoder(elemjson);
    tmpArray[i / cols].push_back(val);
  }

  array.swap(tmpArray);
  return true;
}

template <typename Type>
json_t* SerializeArray2d(const Array2d<Type>& array, EncodeFunc<Type> encoder)
{
  json_t* array2djson = json_object();
  json_t* rowsjson = json_integer(array.size());
  json_t* colsjson = json_integer(array[0].size());

  json_object_set(array2djson, "rows", rowsjson);
  json_object_set(array2djson, "colums", colsjson);

  json_t* datajson = json_array();
  for (const auto& row : array)
  {
    for (const Type& val : row)
    {
      json_t* elemjson = encoder(val);
      json_array_append(datajson, elemjson);
    }
  }

  json_object_set(array2djson, "data", datajson);
  return array2djson;
}

template <typename Type>
bool SetArray2d(Array2d<Type>& array, size_t row, size_t col, const Type& value)
{
  if (row >= array.size()) return false;
  if (col >= array[row].size()) return false;

  array[row][col] = value;
  return true;
}

template <typename Type>
bool SwapArray2d(Array2d<Type>& array, size_t r1, size_t c1, size_t r2, size_t c2)
{
  if (r1 >= array.size()) return false;
  if (c1 >= array[r1].size()) return false;
  if (r2 >= array.size()) return false;
  if (c2 >= array[c2].size()) return false;

  std::swap(array[r1][c1], array[r2][c2]);
  return true;
}

template <typename Type>
void PrintArray2d(const Array2d<Type>& array)
{
  for (const auto& vec : array)
  {
    for (const auto& val : vec)
    {
      std::cout << val << " ";
    }
  }
  std::cout << "\n";
}

#endif
