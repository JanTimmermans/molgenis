package org.molgenis.data.annotation.entity;

import java.util.List;

import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.Entity;
import org.molgenis.data.annotation.CmdLineAnnotatorSettingsConfigurer;

/**
 * Simple interface to implement for fine-grained annotators.
 * 
 * @author fkelpin
 *
 */
public interface EntityAnnotator extends Annotator
{
	/**
	 * Annotates a single entity.
	 * 
	 * @param sourceEntity
	 *            the entity to annotate
	 * @return {@link Iterable} for the annotated data
	 */
	Iterable<Entity> annotateEntity(Entity sourceEntity);

	/**
	 * @return the {@link List<AttributeMetaData>} that must be present in the source entity.
	 */
	List<AttributeMetaData> getRequiredAttributes();

	// TODO: plus aanwijzing wat er ontbreekt
	/**
	 * @return Indication if the required source repository is present in the dataService
	 */
	boolean sourceExists();

	public CmdLineAnnotatorSettingsConfigurer getCmdLineAnnotatorSettingsConfigurer();
}
