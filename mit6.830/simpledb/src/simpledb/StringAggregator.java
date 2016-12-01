package simpledb;

import java.util.ArrayList;
import java.util.Hashtable;

import simpledb.Aggregator.Op;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final StringField nonaggField = new StringField("~NoAggregation~", 20);

    private int _gbfield;
    private Type _gbfieldtype;
    private int _afield;
    private Op _what;
    private Hashtable<Field, Integer> _count;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        _gbfield = gbfield;
        _gbfieldtype = gbfieldtype;
        _afield = afield;
        _what = what;
        _count = new Hashtable<Field, Integer>();
        if (_what != Op.COUNT) {
            throw new IllegalArgumentException(
                    "Only Supprot Count for string aggregation");
        }
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void merge(Tuple tup) {
        // some code goes here
        Field gbf = null;
        if (_gbfield == Aggregator.NO_GROUPING) {
            gbf = nonaggField;
        } else {
            gbf = tup.getField(_gbfield);
        }
        StringField sf = (StringField) tup.getField(_afield);
        aggregate(gbf, sf);
    }
    
    private void aggregate(Field gbf, StringField sf) {
        if (!_count.containsKey(gbf)) {
            _count.put(gbf, 1);
        } else {
            _count.put(gbf, _count.get(gbf) + 1);
        }
    }


    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        ArrayList<Tuple> tup = new ArrayList<Tuple>();
        TupleDesc td = null;
        
        if (_gbfield == Aggregator.NO_GROUPING) {
            Type[] tarr = new Type[1];
            String[] farr = new String[1];
            tarr[0] = Type.INT_TYPE;
            farr[0] = null;
            td = new TupleDesc(tarr, farr);
            Tuple t = new Tuple(td);
            setTupleField(t, 0, nonaggField);
            tup.add(t);
        } else {
            Type[] tarr = new Type[2];
            String[] farr = new String[2];
            tarr[0] = _gbfieldtype;
            tarr[1] = Type.INT_TYPE;
            farr[0] = null;
            farr[1] = null;
            td = new TupleDesc(tarr, farr);
            
            for (Field key : _count.keySet()) {
                Tuple t = new Tuple(td);
                t.setField(0, key);
                setTupleField(t, 1, key);
                tup.add(t);
            }
        }
        
        return new TupleIterator(td, tup);
    }
    
    
    // Set field at pos with key according to _what
    private void setTupleField(Tuple t, int pos, Field key) {
        t.setField(pos, new IntField(_count.get(key)));
    }

}
