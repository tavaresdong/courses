package simpledb;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {
	
    private final Type[]   _fieldType;
    private final String[] _fieldName;

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields
     * fields, with the first td1.numFields coming from td1 and the remaining
     * from td2.
     * @param td1 The TupleDesc with the first fields of the new TupleDesc
     * @param td2 The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc combine(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        Type[] types = new Type[td1.numFields() + td2.numFields()];
        String[] names = new String[td1.numFields() + td2.numFields()];
        int i = 0;
        for (; i < td1.numFields(); i++) {
            types[i] = td1.getType(i);
            names[i] = td1.getFieldName(i);
        }
        for (int j = 0; j < td2.numFields(); j++) {
            types[i + j] = td2.getType(j);
            names[i + j] = td2.getFieldName(j);
        }
        return new TupleDesc(types, names);
    }

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        if (typeAr == null || typeAr.length == 0)
            throw new IllegalArgumentException("Argument for TupleDesc illegal!");
        _fieldType = new Type[typeAr.length];
        _fieldName   = new String[typeAr.length];
        for (int i = 0; i < typeAr.length; i++) {
            _fieldType[i] = typeAr[i];
            _fieldName[i] = fieldAr[i];
        }
    }

    /**
     * Constructor.
     * Create a new tuple desc with typeAr.length fields with fields of the
     * specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        if (typeAr == null || typeAr.length == 0)
            throw new IllegalArgumentException("Argument for TupleDesc illegal!");
        _fieldType = new Type[typeAr.length];
        _fieldName   = new String[typeAr.length];
        for (int i = 0; i < typeAr.length; i++) {
            _fieldType[i] = typeAr[i];
            _fieldName[i] = null;
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return _fieldType.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= _fieldName.length)
            throw new NoSuchElementException("Index out of bounds: " + i);
        return _fieldName[i];
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
        // some code goes here
        if (name == null)
            throw new NoSuchElementException();
        for (int i = 0; i < _fieldName.length; i++) {
            if (_fieldName[i] != null && _fieldName[i].equals(name)) {
                return i;
            }
        }
        throw new NoSuchElementException("No field with name: " + name);
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= _fieldType.length)
            throw new NoSuchElementException();
        return _fieldType[i];
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        int len = 0;
        for (Type t : _fieldType)
            len += t.getLen();
        return len;
    }

    /**
     * Compares the specified object with this TupleDesc for equality.
     * Two TupleDescs are considered equal if they are the same size and if the
     * n-th type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        if (this == o)
            return true;
        if (!(o instanceof TupleDesc))
            return false;
        TupleDesc other = (TupleDesc) o;
        if (numFields() != other.numFields()) {
            return false;
        }
        for (int i = 0; i < numFields(); i++) {
            if (_fieldType[i] != other.getType(i)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _fieldType.length; i++) {
            Type t = _fieldType[i];
            String name = _fieldName[i];
            if (i != 0)
                sb.append(",");
            sb.append(t.toString() + "[" + i + "]" + "(" + name + ")");
        }
        return sb.toString();
    }
}
