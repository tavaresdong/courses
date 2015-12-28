import sys, os
from itertools import izip



class IBM_Model2:
    def __init__(self, tmodel_path = 'tmodel'):
        self.tmodel_path = tmodel_path

    def train_model(self, native_path, foreign_path, iter_count = 5):
        self._load_tmodel()
        self.q = {}
        native_file = file(native_path)
        foreign_file = file(foreign_path)        
        native_file.seek(0); 
        foreign_file.seek(0)
        self.native_word_count = {}
        for line in native_file:
            line = '_NULL_ ' + line
            ntokens = line.rstrip().split()
            for word in ntokens:
                self.native_word_count[word] = self.native_word_count.get(word, 0) + 1
        self._iteration(native_file, foreign_file, iter_count)
        print 'Training Done.\r\n'

    def _load_tmodel(self):
        self.t = {}
        with open(self.tmodel_path, 'r') as tmodel_file:
            for line in tmodel_file:
                tokens = line.rstrip().split()
                e, f, val = tokens[0], tokens[1], tokens[2]
                self.t[(e, f)] = float(val)


    def do_alignment(self, na_path, fo_path, out_path):
        na_file, fo_file = file(na_path), file(fo_path)
        na_file.seek(0) 
        fo_file.seek(0)
        with open(out_path, 'w') as out_file:
            sentence_num = 1
            for na_line, fo_line in izip(na_file, fo_file):
                na_line = '_NULL ' + na_line
                na_tokens = na_line.rstrip().split()
                fo_tokens = fo_line.rstrip().split()
                result = self._align_sentence(na_tokens, fo_tokens, sentence_num, out_file)
                sentence_num += 1
        print 'Alignment done\r\n'

    def save_model(self, path):
        with open(path, 'w') as save_file:
            for (e, f), val in self.t.iteritems():
                save_file.write('%s %s %f\r\n'%(e, f, val))
        

    def load_model(self, path):
        self.t = {}
        with open(path, 'r') as tmodel_file:
            for line in tmodel_file:
                tokens = line.rstrip().split()
                e, f, val = tokens[0], tokens[1], tokens[2]
                self.t[(e, f)] = float(val)

    def _iteration(self, native_file, foreign_file, iter_count):
        for iternum in range(0, iter_count):
            print 'iter%d\r\n'%iternum
            cfe, ce, cjilm, cilm = {}, {}, {}, {}
            native_file.seek(0); 
            foreign_file.seek(0)
            #iter lines
            for na_line, fo_line in izip(native_file, foreign_file):
                na_line = '_NULL_ ' + na_line
                na_tokens = na_line.rstrip().split()
                fo_tokens = fo_line.rstrip().split()
                l, m = len(fo_tokens), len(na_tokens)
                for i in range(len(fo_tokens)):
                    for j in range(len(na_tokens)):
                        na, fo = na_tokens[j], fo_tokens[i]
                        delta = self._calculate_delta(na_tokens, fo_tokens, j, i) 
                        cfe[(na, fo)] = cfe.get((na, fo), 0.0) + delta
                        ce[na] = ce.get(na, 0.0) + delta
                        cjilm[(j, i, l, m)] = cjilm.get((j, i, l, m), 0.0) + delta
                        cilm[(i, l, m)] = cilm.get((i, l, m), 0.0) + delta
            #update t parameter
            for (na, fo), count in cfe.iteritems():
                self.t[(fo, na)] = float(count)/ce.get(na, 0.0)
            for (j, i, l, m), count in cjilm.iteritems():
                self.q[(j, i, l, m)] = float(count) / cilm.get((i, l, m), 0.0)
            print 'iter%d done.\r\n'%iternum
        

    def _calculate_delta(self, na_tokens, fo_tokens, j, i):
        na, fo = na_tokens[j], fo_tokens[i]
        l, m = len(fo_tokens), len(na_tokens)
        tval = self.t.get((fo, na), 1.0 / self.native_word_count[na])
        qval = self.q.get((j, i, l, m), 1.0 / (l + 1))
        sumterm = 0.0
        for q in range(0, len(na_tokens)):
            n, f = na_tokens[q], fo_tokens[i]
            term = self.q.get((q, i, l, m), 1.0 / (l + 1)) * self.t.get((f, n), 1.0 / self.native_word_count[na])
            sumterm += term
        return float(tval * qval / sumterm)



    def _align_sentence(self, na_tokens, fo_tokens, sentence_num, out_file):
        res = []
        for i in range(0, len(fo_tokens)):
            max_ind = 0; max_score = 0.0
            for j in range(0, len(na_tokens)):
                na, fo = na_tokens[j], fo_tokens[i]
                l, m = len(fo_tokens), len(na_tokens)
                score = self.t.get((fo, na), 0.0) * self.q.get((j, i, l, m), 0.0)
                if score > max_score:
                    max_ind, max_score = j, score
            res.append(max_ind)
        fo_num = 1
        # Write the align result to out file
        for a in res:
            out_file.write('%d %d %d\r\n'%(sentence_num, a, fo_num))
            fo_num += 1
                

        
        
if __name__ == '__main__':
    try:
        used_exist_model = False 
        ibm_model2 = IBM_Model2('ibm.tmodel2')
        ibm_model2.train_model('corpus.ch', 'corpus.en', 5)
        ibm_model2.do_alignment('test.ch.txt', 'test.en.txt', 'ibm2.out')
        print 'parsing done'
        
    except Exception, e:
        print e
    finally:
        os.system('pause')
