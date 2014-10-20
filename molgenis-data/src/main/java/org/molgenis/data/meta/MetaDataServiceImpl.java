package org.molgenis.data.meta;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.ManageableCrudRepositoryCollection;
import org.molgenis.data.Package;

/**
 * MetaData service. Administration of the {@link Package}, {@link EntityMetaData} and {@link AttributeMetaData} of the
 * metadata of the repositories.
 * 
 * <img src="http://yuml.me/8870d0e4.png" alt="Metadata entities" width="640"/>
 */
public class MetaDataServiceImpl implements WritableMetaDataService
{
	private PackageRepository packageRepository;
	private EntityMetaDataRepository entityMetaDataRepository;
	private AttributeMetaDataRepository attributeMetaDataRepository;
	private static final Logger LOG = Logger.getLogger(MetaDataServiceImpl.class);

	/**
	 * Setter for the MysqlRepositoryCollection, to be called after it's created. This resolves the circular dependency
	 * {@link MysqlRepositoryCollection} => decorated {@link WritableMetaDataService} => {@link RepositoryCreator}
	 * 
	 * @param mysqlRepositoryCollection
	 */
	public void setManageableCrudRepositoryCollection(ManageableCrudRepositoryCollection repositoryCreator)
	{
		if (repositoryCreator != null)
		{
			packageRepository = new PackageRepository(repositoryCreator);
			entityMetaDataRepository = new EntityMetaDataRepository(repositoryCreator, packageRepository);
			attributeMetaDataRepository = new AttributeMetaDataRepository(repositoryCreator, entityMetaDataRepository);
		}
	}

	/**
	 * Removes entity meta data if it exists.
	 */
	@Override
	public void removeEntityMetaData(String entityName)
	{
		attributeMetaDataRepository.deleteAllAttributes(entityName);
		entityMetaDataRepository.delete(entityName);
	}

	/**
	 * Removes an attribute from an entity.
	 */
	@Override
	public void removeAttributeMetaData(String entityName, String attributeName)
	{
		// Update AttributeMetaDataRepository
		attributeMetaDataRepository.remove(entityName, attributeName);
	}

	@Override
	public void addEntityMetaData(EntityMetaData emd)
	{
		if (attributeMetaDataRepository == null)
		{
			return;
		}

		if (emd.getPackage() != null)
		{
			packageRepository.add(emd.getPackage());
		}

		Entity mdEntity = entityMetaDataRepository.add(emd);

		// add attribute metadata
		for (AttributeMetaData att : emd.getAttributes())
		{
			if (LOG.isTraceEnabled())
			{
				LOG.trace("Adding attribute metadata for entity " + emd.getName() + ", attribute " + att.getName());
			}
			attributeMetaDataRepository.add(mdEntity, att);
		}
	}

	@Override
	public void addAttributeMetaData(String fullyQualifiedName, AttributeMetaData attr)
	{
		Entity entity = entityMetaDataRepository.getEntity(fullyQualifiedName);
		entityMetaDataRepository.get(fullyQualifiedName).addAttributeMetaData(attr);
		attributeMetaDataRepository.add(entity, attr);
	}

	@Override
	public EntityMetaData getEntityMetaData(String fullyQualifiedName)
	{
		// at construction time, will be called when entityMetaDataRepository is still null
		if (attributeMetaDataRepository == null)
		{
			return null;
		}
		return entityMetaDataRepository.get(fullyQualifiedName);
	}

	@Override
	public void addPackage(Package p)
	{
		packageRepository.add(p);
	}

	@Override
	public Package getPackage(String string)
	{
		return packageRepository.getPackage(string);
	}

	@Override
	public List<Package> getRootPackages()
	{
		return packageRepository.getRootPackages();
	}

	/**
	 * Empties all metadata tables for the sake of testability.
	 */
	public void recreateMetaDataRepositories()
	{
		attributeMetaDataRepository.deleteAll();
		entityMetaDataRepository.deleteAll();
		packageRepository.deleteAll();
		packageRepository.updatePackageCache();
	}

	@Override
	public Collection<EntityMetaData> getEntityMetaDatas()
	{
		return entityMetaDataRepository.getMetaDatas();
	}
}