package org.teiath.service.act;

import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import java.util.Collection;

public interface CopyActionService {

	public void saveEvent(Event event, ListModelList<AImage> uploadedImages, ListModelList<EventPDF> uploadedPDFs)
			throws ServiceException;

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public Event getEventById(Integer eventId)
			throws ServiceException;

	public Collection<ApplicationImage> getImages(Event event)
			throws ServiceException;

	public Collection<EventPDF> getPDFs(Event event)
			throws ServiceException;

	public void deleteEventPhotos(Event event)
			throws ServiceException;

	public void deleteEventPDFs(Event event)
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;

	public ListModelList<String> getPopularLocations()
			throws ServiceException;

	public Collection<Currency> getCurrencies()
			throws ServiceException;
}
