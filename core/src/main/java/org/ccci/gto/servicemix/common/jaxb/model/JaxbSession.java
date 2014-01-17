package org.ccci.gto.servicemix.common.jaxb.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.ccci.gto.servicemix.common.model.Session;

@XmlRootElement(name = "session")
public class JaxbSession {
    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "guid")
    private String guid;

    public JaxbSession() {
    }

    public JaxbSession(final Session session) {
        this.id = session.getId();
        this.guid = session.getGuid();
    }
}
