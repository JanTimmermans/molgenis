package org.molgenis.omx.biobankconnect.ontologytree;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.omx.biobankconnect.ontologyservice.OntologyService;
import org.molgenis.omx.biobankconnect.utils.OntologyRepository;
import org.molgenis.search.Hit;
import org.molgenis.search.SearchRequest;
import org.molgenis.search.SearchResult;
import org.molgenis.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;

public class OntologyIndexRepository extends AbstractOntologyIndexRepository
{
	public final static String DEFAULT_ONTOLOGY_REPO = "ontologyindex";
	private final static String BASE_URL = "ontologyindex://";
	private final OntologyRepository ontologyRepository;
	private final OntologyService ontologySerivce;

	@Autowired
	public OntologyIndexRepository(String entityName, OntologyService ontologyService, SearchService searchService)
	{
		super(entityName, searchService);
		this.ontologyRepository = new OntologyRepository(null, entityName);
		this.ontologySerivce = ontologyService;
	}

	@Override
	public Iterable<Entity> findAll(Query q)
	{
		List<Entity> entities = new ArrayList<Entity>();
		if (q.getRules().size() > 0) q.and();
		q.eq(OntologyRepository.ENTITY_TYPE, OntologyRepository.TYPE_ONTOLOGY);
		for (Hit hit : searchService.search(new SearchRequest(null, q, null)).getSearchHits())
		{
			entities.add(new OntologyIndexEntity(hit, getEntityMetaData(), ontologySerivce, searchService));
		}
		return entities;
	}

	@Override
	public Entity findOne(Query q)
	{
		Hit hit = findOneInternal(q);
		if (hit != null) return new OntologyIndexEntity(hit, getEntityMetaData(), ontologySerivce, searchService);
		return null;
	}

	@Override
	public Entity findOne(Object id)
	{
		Hit hit = searchService.searchById(null, id.toString());
		if (hit != null) return new OntologyIndexEntity(hit, getEntityMetaData(), ontologySerivce, searchService);
		return null;
	}

	@Override
	public long count(Query q)
	{
		if (q.getRules().size() == 0)
		{
			SearchResult result = searchService.search(new SearchRequest(null, new QueryImpl().eq(
					OntologyRepository.ENTITY_TYPE, OntologyRepository.TYPE_ONTOLOGY), null));

			return result.getTotalHitCount();
		}
		return Iterables.size(findAll(q));
	}

	@Override
	public String getUrl()
	{
		return BASE_URL + ontologyRepository.getName();
	}
}