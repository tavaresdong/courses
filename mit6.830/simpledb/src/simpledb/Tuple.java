package simpledb;

/**
 * Tuple maintains information about the contents of a tuple.
 * Tuples have a specified schema specified by a TupleDesc object and contain
 * Field objects with the data for each field.
 */
public class Tuple {
    private TupleDesc _td;
    private RecordId _rid;
    private Field[] _fields;

    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td the schema of this tuple. It must be a valid TupleDesc
     * instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        if (td == null || td.numFields() == 0)
            throw new IllegalArgumentException(
                           "Tuple Desc needs to contain at least one field");
        _td = td;
        _fields = new Field[td.numFields()];
 
        for (int i = 0; i < td.numFields(); i++) {
            Type t = td.getType(i);
            if (t == Type.INT_TYPE) {
                _fields[i] = new IntField(0);
            } else if (t == Type.STRING_TYPE) {
                _fields[i] = new StringField("", t.getLen());
            }
        }
        _rid = null;
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return _td;
    }

    /**
     * @return The RecordId representing the location of this tuple on
     *   disk. May be null.
     */
    public RecordId getRecordId() {
        // some code goes here
        return _rid;
    }

    /**
     * Set the RecordId information for this tuple.
     * @param rid the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // some code goes here
        _rid = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i index of the field to change. It must be a valid index.
     * @param f new value for the field.
     */
    public void setField(int i, Field f) {
        // some code goes here
        if (i < 0 || i >= _td.numFields()) {
            throw new IllegalArgumentException("Index out of bounds: " + i);
        }
        
        _fields[i] = f;
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     *
     * @param i field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here
        if (i < 0 || i >= _td.numFields()) {
            throw new IllegalArgumentException("Index out of bounds: " + i);
        }
        return _fields[i];
    }

    /**
     * Returns the contents of this Tuple as a string.
     * Note that to pass the system tests, the format needs to be as
     * follows:
     *
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     *
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // some code goes here
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _td.numFields(); i++) {
            if (i != 0)
                sb.append("\t");
            sb.append(_fields[i]);
        }
        sb.append("\n");
        return sb.toString();
    }
}
