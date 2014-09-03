package org.molgenis.data.mysql;

import static org.molgenis.data.mysql.EntityMetaDataMetaData.ABSTRACT;
import static org.molgenis.data.mysql.EntityMetaDataMetaData.DESCRIPTION;
import static org.molgenis.data.mysql.EntityMetaDataMetaData.EXTENDS;
import static org.molgenis.data.mysql.EntityMetaDataMetaData.ID_ATTRIBUTE;
import static org.molgenis.data.mysql.EntityMetaDataMetaData.LABEL;
import static org.molgenis.data.mysql.EntityMetaDataMetaData.NAME;

import java.util.List;

import javax.sql.DataSource;

import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.data.Query;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.molgenis.data.support.MapEntity;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.data.validation.EntityValidator;

import com.google.common.collect.Lists;

public class EntityMetaDataRepository extends MysqlRepository
{
	public static final EntityMetaDataMetaData META_DATA = new EntityMetaDataMetaData();

	public EntityMetaDataRepository(DataSource dataSource, EntityValidator entityValidator)
	{
		super(dataSource, entityValidator);
		setMetaData(META_DATA);
	}

	public List<DefaultEntityMetaData> getEntityMetaDatas()
	{
		List<DefaultEntityMetaData> meta = Lists.newArrayList();
		for (Entity entity : this)
		{
			meta.add(toEntityMetaData(entity));
		}

		return meta;
	}
	
	/**
	 * Gets all EntityMetaData in a package
	 * @param packageName the name of the package
	 */
	public List<DefaultEntityMetaData> getEntityMetaDatas(String packageName)
	{
		List<DefaultEntityMetaData> meta = Lists.newArrayList();
		for (Entity entity : this)
		{
			meta.add(toEntityMetaData(entity));
		}

		return meta;
	}

	/**
	 * Retrieves an EntityMetaData.
	 * @param name the fully qualified name of the entity
	 */
	public DefaultEntityMetaData getEntityMetaData(String fullyQualifiedName)
	{
		Query q = new QueryImpl().eq(EntityMetaDataMetaData.ENTITY_NAME, getEntityName(fullyQualifiedName)).and()
				.eq(EntityMetaDataMetaData.PACKAGE, getPackageName(fullyQualifiedName));
		Entity entity = findOne(q);
		if (entity == null)
		{
			return null;
		}

		return toEntityMetaData(entity);
	}
	
	private String getPackageName(String fullyQualifiedName){
		int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
		return fullyQualifiedName.substring(0, lastDotIndex);
	}
	
	private String getEntityName(String fullyQualifiedName){
		int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
		return fullyQualifiedName.substring(lastDotIndex + 1);
	}

	private DefaultEntityMetaData toEntityMetaData(Entity entity)
	{
		String name = entity.getString(NAME);
		DefaultEntityMetaData entityMetaData = new DefaultEntityMetaData(name);
		entityMetaData.setAbstract(entity.getBoolean(ABSTRACT));
		entityMetaData.setIdAttribute(entity.getString(ID_ATTRIBUTE));
		entityMetaData.setLabel(entity.getString(LABEL));
		entityMetaData.setDescription(entity.getString(DESCRIPTION));

		// Extends
		String extendsEntityName = entity.getString(EXTENDS);
		if (extendsEntityName != null)
		{
			EntityMetaData extendsEmd = getEntityMetaData(extendsEntityName);
			if (extendsEmd == null) throw new MolgenisDataException("Missing super entity [" + extendsEntityName
					+ "] of entity [" + name + "]");
			entityMetaData.setExtends(extendsEmd);
		}

		return entityMetaData;
	}

	public void addEntityMetaData(EntityMetaData emd)
	{
		Entity entityMetaDataEntity = new MapEntity();
		entityMetaDataEntity.set(NAME, emd.getName());
		entityMetaDataEntity.set(DESCRIPTION, emd.getDescription());
		entityMetaDataEntity.set(ABSTRACT, emd.isAbstract());
		if (emd.getIdAttribute() != null) entityMetaDataEntity.set(ID_ATTRIBUTE, emd.getIdAttribute().getName());
		entityMetaDataEntity.set(LABEL, emd.getLabel());
		if (emd.getExtends() != null) entityMetaDataEntity.set(EXTENDS, emd.getExtends().getName());

		add(entityMetaDataEntity);
	}

}
