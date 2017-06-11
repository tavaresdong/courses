/*
 * Copyright 2011 Steven Gribble
 *
 *  This file is part of the UW CSE 333 course project sequence
 *  (333proj).
 *
 *  333proj is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  333proj is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with 333proj.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <iostream>
#include <algorithm>
#include <map>
#include <memory>

#include "./QueryProcessor.h"

extern "C" {
  #include "./libhw1/CSE333.h"
}

namespace hw3 {

QueryProcessor::QueryProcessor(list<string> indexlist, bool validate) {
  // Stash away a copy of the index list.
  indexlist_ = indexlist;
  arraylen_ = indexlist_.size();
  Verify333(arraylen_ > 0);

  // Create the arrays of DocTableReader*'s. and IndexTableReader*'s.
  dtr_array_ = new DocTableReader *[arraylen_];
  itr_array_ = new IndexTableReader *[arraylen_];

  // Populate the arrays with heap-allocated DocTableReader and
  // IndexTableReader object instances.
  list<string>::iterator idx_iterator = indexlist_.begin();
  for (HWSize_t i = 0; i < arraylen_; i++) {
    FileIndexReader fir(*idx_iterator, validate);
    dtr_array_[i] = new DocTableReader(fir.GetDocTableReader());
    itr_array_[i] = new IndexTableReader(fir.GetIndexTableReader());
    idx_iterator++;
  }
}

QueryProcessor::~QueryProcessor() {
  // Delete the heap-allocated DocTableReader and IndexTableReader
  // object instances.
  Verify333(dtr_array_ != nullptr);
  Verify333(itr_array_ != nullptr);
  for (HWSize_t i = 0; i < arraylen_; i++) {
    delete dtr_array_[i];
    delete itr_array_[i];
  }

  // Delete the arrays of DocTableReader*'s and IndexTableReader*'s.
  delete[] dtr_array_;
  delete[] itr_array_;
  dtr_array_ = nullptr;
  itr_array_ = nullptr;
}

vector<QueryProcessor::QueryResult>
QueryProcessor::ProcessQuery(const vector<string> &query) {
  Verify333(query.size() > 0);
  vector<QueryProcessor::QueryResult> finalresult;

  // MISSING:
  // For each index

  for (size_t i = 0; i < arraylen_; ++i) {
    std::map<std::string, HWSize_t> ranks;
    DocTableReader* dReader = dtr_array_[i];
    IndexTableReader* iReader = itr_array_[i];

    for (size_t j = 0; j < query.size(); ++j) {
      std::string word = query[j];
      auto diReader = std::shared_ptr<DocIDTableReader>(iReader->LookupWord(word));
      if (diReader.get() == nullptr) {
        ranks.clear();
        break;
      }

      auto dilist = diReader->GetDocIDList();
      if (dilist.empty()) {
        ranks.clear();
        break;
      }

      std::map<std::string, HWSize_t> tmpranks;
      for (const auto& docidheader : dilist) {
        DocID_t docid = docidheader.docid;
        std::string docname;
        HWSize_t num_positions = docidheader.num_positions;
        Verify333(dReader->LookupDocID(docid, &docname));
        if (j == 0 || (num_positions > 0 && ranks.count(docname))) {
          // If all previous words in the query
          // are present in this docname, then we
          // proceed to accumulate the rank
          tmpranks[docname] = ranks[docname] + num_positions;
        }
      }

      tmpranks.swap(ranks);
    }

    for (const auto& kv : ranks) {
      finalresult.push_back(QueryResult(kv.first, kv.second));
    }
  }


  // Sort the final results.
  std::sort(finalresult.begin(), finalresult.end());
  return finalresult;
}

}  // namespace hw3
