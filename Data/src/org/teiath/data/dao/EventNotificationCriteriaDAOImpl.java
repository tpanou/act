package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

import java.util.Collection;

@Repository("eventNotificationCriteriaDAO")
public class EventNotificationCriteriaDAOImpl
		implements EventNotificationCriteriaDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void save(EventNotificationCriteria eventNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(eventNotificationCriteria);
	}

	@Override
	public void delete(EventNotificationCriteria eventNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(eventNotificationCriteria);
	}

	@Override
	public Collection<EventNotificationCriteria> checkCriteria(Event event) {
		Session session = sessionFactory.getCurrentSession();
		Collection<EventNotificationCriteria> eventNotificationCriterias;
		Criteria criteria = session.createCriteria(EventNotificationCriteria.class);


		//Event categories restriction
		criteria.createAlias("eventCategories", "categories");
		Junction junc = Restrictions.conjunction();
		junc.add(Restrictions.eq("categories.id", event.getEventCategory().getId()));
		criteria.add(junc);

		//Date restriction
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("dateFrom"), Restrictions.isNotNull("id")),
				Restrictions.le("dateFrom", event.getDateFrom())));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("dateTo"), Restrictions.isNotNull("id")),
				Restrictions.ge("dateTo", event.getDateTo())));

		//AMEA access restriction
		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.eq("disabledAccess", false), Restrictions.isNotNull("id")),
						Restrictions.eq("disabledAccess", true)));

		//Keyword restriction
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("keywords"), Restrictions.isNotNull("id")),
				Restrictions.and(Restrictions.ne("keywords", ""),
						Restrictions.like("keywords", event.getEventTitle(), MatchMode.ANYWHERE),
						Restrictions.like("keywords", event.getEventDescription(), MatchMode.ANYWHERE))));

		eventNotificationCriterias = criteria.list();

		return eventNotificationCriterias;
	}

	@Override
	public EventNotificationCriteria findById(Integer id) {
		EventNotificationCriteria eventNotificationCriteria;

		Session session = sessionFactory.getCurrentSession();
		eventNotificationCriteria = (EventNotificationCriteria) session.get(EventNotificationCriteria.class, id);

		return eventNotificationCriteria;
	}

	@Override
	public SearchResults<EventNotificationCriteria> search(
			NotificationsCriteriaSearchCriteria notificationsCriteriaSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<EventNotificationCriteria> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(EventNotificationCriteria.class);

		//Type
		if (notificationsCriteriaSearchCriteria.getType() != null) {
			criteria.add(Restrictions.eq("type", notificationsCriteriaSearchCriteria.getType()));
		}

		//User
		if (notificationsCriteriaSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("user", notificationsCriteriaSearchCriteria.getUser()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(
				notificationsCriteriaSearchCriteria.getPageNumber() * notificationsCriteriaSearchCriteria
						.getPageSize());
		criteria.setMaxResults(notificationsCriteriaSearchCriteria.getPageSize());

		//Sorting
		if (notificationsCriteriaSearchCriteria.getOrderField() != null) {
			if (notificationsCriteriaSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(notificationsCriteriaSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(notificationsCriteriaSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}
}
