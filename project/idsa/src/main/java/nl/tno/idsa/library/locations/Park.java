package nl.tno.idsa.library.locations;

import nl.tno.idsa.framework.semantics_impl.locations.LocationFunction;

public class Park extends LocationFunction {

    public Park() {
    }

    public Park(int capacity) {
        super(capacity);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends LocationFunction>[] getSuperclassArray() {
        return new Class[]{Outside.class, Public.class};
    }
}
