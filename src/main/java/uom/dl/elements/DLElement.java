package uom.dl.elements;

public interface DLElement {
	public boolean isAtomic();
	/**
	 * Checks if the {@link DLElement} can have a complement {@link DLElement} which may
	 * cause a clash if t exists. 
	 * @return true if the {@link DLElement} can have a complement. 
	 */
	public boolean canHaveComplement();
	public boolean isComplement(DLElement other);
}
