/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.html.org.apache.any23.vocab;

import org.eclipse.rdf4j.model.IRI;

/**
 * Vocabulary definitions from <code>ical.rdf</code>
 */
public class ICAL extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.w3.org/2002/12/cal/icaltzd#";

    /**
     * Recommended prefix for the ICAL namespace
     */
    public static final String PREFIX = "ical";

    private static final class InstanceHolder {
        private static final ICAL instance = new ICAL();
    }

    public static ICAL getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * The namespace of the vocabulary as a IRI.
     */
    public final IRI NAMESPACE = createIRI(NS);

    public final IRI DomainOf_rrule = createClass("DomainOf_rrule");
    public final IRI List_of_Float = createClass("List_of_Float");

    /**
     * Provide a grouping of component properties that define an alarm..
     */
    public final IRI Valarm = createClass("Valarm");

    public final IRI Value_CAL_ADDRESS = createClass("Value_CAL-ADDRESS");

    public final IRI Value_DATE = createClass("Value_DATE");

    public final IRI Value_DATE_TIME = createClass("Value_DATE-TIME");

    /**
     * ICAL datatype for floating date-time values
     */
    public final IRI DATE_TIME = createIRI(NS + "dateTime");

    public final IRI Value_DURATION = createClass("Value_DURATION");

    public final IRI Value_PERIOD = createClass("Value_PERIOD");

    public final IRI Value_RECUR = createClass("Value_RECUR");

    public final IRI Vcalendar = createClass("Vcalendar");

    /**
     * Provide a grouping of component properties that describe an event..
     */
    public final IRI Vevent = createClass("Vevent");

    /**
     * Provide a grouping of component properties that describe either a request for free/busy time, describe a response
     * to a request for free/busy time or describe a published set of busy time..
     */
    public final IRI Vfreebusy = createClass("Vfreebusy");

    /**
     * Provide a grouping of component properties that describe a journal entry..
     */
    public final IRI Vjournal = createClass("Vjournal");

    /**
     * Provide a grouping of component properties that defines a time zone..
     */
    public final IRI Vtimezone = createClass("Vtimezone");

    /**
     * Provide a grouping of calendar properties that describe a to-do..
     */
    public final IRI Vtodo = createClass("Vtodo");

    /**
     * The IRI provides the capability to associate a document object with a calendar component.default value type: IRI.
     */
    public final IRI attach = createProperty("attach");

    /**
     * The IRI defines an "Attendee" within a calendar component.value type: CAL-ADDRESS.
     */
    public final IRI attendee = createProperty("attendee");
    public final IRI calAddress = createProperty("calAddress");
    public final IRI component = createProperty("component");
    public final IRI daylight = createProperty("daylight");

    /**
     * The IRI specifies a positive duration of time.value type: DURATION.
     */
    public final IRI duration = createProperty("duration");

    /**
     * This IRI defines a rule or repeating pattern for an exception to a recurrence set.value type: RECUR.
     */
    public final IRI exrule = createProperty("exrule");

    /**
     * The IRI defines one or more free or busy time intervals.value type: PERIOD.
     */
    public final IRI freebusy = createProperty("freebusy");

    /**
     * value type: list of FLOATThis IRI specifies information related to the global position for the activity specified
     * by a calendar component..
     */
    public final IRI geo = createProperty("geo");

    /**
     * value type: CAL-ADDRESSThe IRI defines the organizer for a calendar component..
     */
    public final IRI organizer = createProperty("organizer");

    /**
     * This IRI defines a rule or repeating pattern for recurring events, to-dos, or time zone definitions.value type:
     * RECUR.
     */
    public final IRI rrule = createProperty("rrule");

    public final IRI standard = createProperty("standard");

    /**
     * This IRI specifies when an alarm will trigger.default value type: DURATION.
     */
    public final IRI trigger = createProperty("trigger");

    /**
     * The TZURL provides a means for a VTIMEZONE component to point to a network location that can be used to retrieve
     * an up-to- date version of itself.value type: IRI.
     */
    public final IRI tzurl = createProperty("tzurl");

    /**
     * This IRI defines a Uniform IRI Locator (URL) associated with the iCalendar object.value type: IRI.
     */
    public final IRI url = createProperty("url");

    /**
     * value type: TEXTThis class of IRI provides a framework for defining non-standard properties..
     */
    public final IRI X_ = createProperty("X-");

    /**
     * value type: TEXTThis IRI defines the action to be invoked when an alarm is triggered..
     */
    public final IRI action = createProperty("action");

    /**
     * To specify an alternate text representation for the IRI value..
     */
    public final IRI altrep = createProperty("altrep");

    public final IRI byday = createProperty("byday");

    public final IRI byhour = createProperty("byhour");

    public final IRI byminute = createProperty("byminute");

    public final IRI bymonth = createProperty("bymonth");

    public final IRI bysecond = createProperty("bysecond");

    public final IRI bysetpos = createProperty("bysetpos");

    public final IRI byweekno = createProperty("byweekno");

    public final IRI byyearday = createProperty("byyearday");

    public final IRI wkst = createProperty("wkst");

    /**
     * value type: TEXTThis IRI defines the calendar scale used for the calendar information specified in the iCalendar
     * object..
     */
    public final IRI calscale = createProperty("calscale");

    /**
     * value type: TEXTThis IRI defines the categories for a calendar component..
     */
    public final IRI categories = createProperty("categories");

    /**
     * value type: TEXTThis IRI defines the access classification for a calendar component..
     */
    public final IRI class_ = createProperty("class");

    /**
     * To specify the common name to be associated with the calendar user specified by the IRI..
     */
    public final IRI cn = createProperty("cn");

    /**
     * value type: TEXTThis IRI specifies non-processing information intended to provide a comment to the calendar
     * user..
     */
    public final IRI comment = createProperty("comment");

    /**
     * value type: DATE-TIMEThis IRI defines the date and time that a to-do was actually completed..
     */
    public final IRI completed = createProperty("completed");

    /**
     * value type: TEXTThe IRI is used to represent contact information or alternately a reference to contact
     * information associated with the calendar component..
     */
    public final IRI contact = createProperty("contact");

    public final IRI count = createProperty("count");

    /**
     * This IRI specifies the date and time that the calendar information was created by the calendar user agent in the
     * calendar store. Note: This is analogous to the creation date and time for a file in the file system.value type:
     * DATE-TIME.
     */
    public final IRI created = createProperty("created");

    /**
     * To specify the type of calendar user specified by the IRI..
     */
    public final IRI cutype = createProperty("cutype");

    /**
     * To specify the calendar users that have delegated their participation to the calendar user specified by the IRI..
     */
    public final IRI delegatedFrom = createProperty("delegatedFrom");

    /**
     * To specify the calendar users to whom the calendar user specified by the IRI has delegated participation..
     */
    public final IRI delegatedTo = createProperty("delegatedTo");

    /**
     * value type: TEXTThis IRI provides a more complete description of the calendar component, than that provided by
     * the "SUMMARY" IRI..
     */
    public final IRI description = createProperty("description");

    /**
     * To specify reference to a directory entry associated with the calendar user specified by the IRI..
     */
    public final IRI dir = createProperty("dir");

    /**
     * This IRI specifies the date and time that a calendar component ends.default value type: DATE-TIME.
     */
    public final IRI dtend = createProperty("dtend");

    /**
     * value type: DATE-TIMEThe IRI indicates the date/time that the instance of the iCalendar object was created..
     */
    public final IRI dtstamp = createProperty("dtstamp");

    /**
     * default value type: DATE-TIMEThis IRI specifies when the calendar component begins..
     */
    public final IRI dtstart = createProperty("dtstart");

    /**
     * default value type: DATE-TIMEThis IRI defines the date and time that a to-do is expected to be completed..
     */
    public final IRI due = createProperty("due");

    /**
     * To specify an alternate inline encoding for the IRI value..
     */
    public final IRI encoding = createProperty("encoding");

    /**
     * default value type: DATE-TIMEThis IRI defines the list of date/time exceptions for a recurring calendar
     * component..
     */
    public final IRI exdate = createProperty("exdate");

    /**
     * To specify the free or busy time type..
     */
    public final IRI fbtype = createProperty("fbtype");

    /**
     * To specify the content type of a referenced object..
     */
    public final IRI fmttype = createProperty("fmttype");

    public final IRI freq = createProperty("freq");

    public final IRI interval = createProperty("interval");

    /**
     * To specify the language for text values in a IRI or IRI parameter..
     */
    public final IRI language = createProperty("language");

    /**
     * value type: DATE-TIMEThe IRI specifies the date and time that the information associated with the calendar
     * component was last revised in the calendar store. Note: This is analogous to the modification date and time for a
     * file in the file system..
     */
    public final IRI lastModified = createProperty("lastModified");

    /**
     * value type: TEXTThe IRI defines the intended venue for the activity defined by a calendar component..
     */
    public final IRI location = createProperty("location");

    /**
     * To specify the group or list membership of the calendar user specified by the IRI..
     */
    public final IRI member = createProperty("member");

    /**
     * value type: TEXTThis IRI defines the iCalendar object method associated with the calendar object..
     */
    public final IRI method = createProperty("method");

    /**
     * To specify the participation status for the calendar user specified by the IRI..
     */
    public final IRI partstat = createProperty("partstat");

    /**
     * value type: INTEGERThis IRI is used by an assignee or delegatee of a to-do to convey the percent completion of a
     * to-do to the Organizer..
     */
    public final IRI percentComplete = createProperty("percentComplete");

    /**
     * The IRI defines the relative priority for a calendar component.value type: INTEGER.
     */
    public final IRI priority = createProperty("priority");

    /**
     * value type: TEXTThis IRI specifies the identifier for the product that created the iCalendar object..
     */
    public final IRI prodid = createProperty("prodid");

    /**
     * To specify the effective range of recurrence instances from the instance specified by the recurrence identifier
     * specified by the IRI..
     */
    public final IRI range = createProperty("range");

    /**
     * default value type: DATE-TIMEThis IRI defines the list of date/times for a recurrence set..
     */
    public final IRI rdate = createProperty("rdate");

    /**
     * default value type: DATE-TIMEThis IRI is used in conjunction with the "UID" and "SEQUENCE" IRI to identify a
     * specific instance of a recurring "VEVENT", "VTODO" or "VJOURNAL" calendar component. The IRI value is the
     * effective value of the "DTSTART" IRI of the recurrence instance..
     */
    public final IRI recurrenceId = createProperty("recurrenceId");

    /**
     * To specify the relationship of the alarm trigger with respect to the start or end of the calendar component..
     */
    public final IRI related = createProperty("related");

    /**
     * The IRI is used to represent a relationship or reference between one calendar component and another.value type:
     * TEXT.
     */
    public final IRI relatedTo = createProperty("relatedTo");

    /**
     * To specify the type of hierarchical relationship associated with the calendar component specified by the IRI..
     */
    public final IRI reltype = createProperty("reltype");

    /**
     * This IRI defines the number of time the alarm should be repeated, after the initial trigger.value type: INTEGER.
     */
    public final IRI repeat = createProperty("repeat");

    /**
     * value type: TEXTThis IRI defines the status code returned for a scheduling request..
     */
    public final IRI requestStatus = createProperty("requestStatus");

    /**
     * value type: TEXTThis IRI defines the equipment or resources anticipated for an activity specified by a calendar
     * entity...
     */
    public final IRI resources = createProperty("resources");

    /**
     * To specify the participation role for the calendar user specified by the IRI..
     */
    public final IRI role = createProperty("role");

    /**
     * To specify whether there is an expectation of a favor of a reply from the calendar user specified by the IRI
     * value..
     */
    public final IRI rsvp = createProperty("rsvp");

    /**
     * To specify the calendar user that is acting on behalf of the calendar user specified by the IRI..
     */
    public final IRI sentBy = createProperty("sentBy");

    /**
     * value type: integerThis IRI defines the revision sequence number of the calendar component within a sequence of
     * revisions..
     */
    public final IRI sequence = createProperty("sequence");

    /**
     * value type: TEXTThis IRI defines the overall status or confirmation for the calendar component..
     */
    public final IRI status = createProperty("status");

    /**
     * This IRI defines a short summary or subject for the calendar component.value type: TEXT.
     */
    public final IRI summary = createProperty("summary");

    /**
     * This IRI defines whether an event is transparent or not to busy time searches.value type: TEXT.
     */
    public final IRI transp = createProperty("transp");

    /**
     * value type: TEXTTo specify the identifier for the time zone definition for a time component in the IRI value.This
     * IRI specifies the text value that uniquely identifies the "VTIMEZONE" calendar component..
     */
    public final IRI tzid = createProperty("tzid");

    /**
     * value type: TEXTThis IRI specifies the customary designation for a time zone description..
     */
    public final IRI tzname = createProperty("tzname");

    /**
     * value type: UTC-OFFSETThis IRI specifies the offset which is in use prior to this time zone observance..
     */
    public final IRI tzoffsetfrom = createProperty("tzoffsetfrom");

    /**
     * value type: UTC-OFFSETThis IRI specifies the offset which is in use in this time zone observance..
     */
    public final IRI tzoffsetto = createProperty("tzoffsetto");

    /**
     * This IRI defines the persistent, globally unique identifier for the calendar component.value type: TEXT.
     */
    public final IRI uid = createProperty("uid");

    public final IRI until = createProperty("until");

    /**
     * value type: TEXTThis IRI specifies the identifier corresponding to the highest version number or the minimum and
     * maximum range of the iCalendar specification that is required in order to interpret the iCalendar object..
     */
    public final IRI version = createProperty("version");

    private IRI createClass(String string) {
        return createClass(NS, string);
    }

    private IRI createProperty(String string) {
        return createProperty(NS, string);
    }

    private ICAL() {
        super(NS);
    }

}
