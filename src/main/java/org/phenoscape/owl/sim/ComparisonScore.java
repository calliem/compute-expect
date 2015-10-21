package org.phenoscape.owl.sim;

/**
 * @author jbalhoff
 *
 * @param <ID> The type used for an identifier (e.g. IRI, URI, String, etc.).
 */
public interface ComparisonScore<ID> {

	/**
	 * @return Unique identifier for this comparison
	 */
	public ID id();

	/**
	 * @return Similarity score for the query and corpus profiles being compared
	 */
	public double similarity();

	/**
	 * @return Identifier for the compared query profile
	 */
	public ID queryProfile();

	/**
	 * @return Identifier for the compared corpus profile
	 */
	public ID corpusProfile();

}
