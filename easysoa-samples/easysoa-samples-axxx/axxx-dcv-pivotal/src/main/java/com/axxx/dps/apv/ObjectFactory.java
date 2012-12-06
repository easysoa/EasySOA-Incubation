
package com.axxx.dps.apv;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.axxx.dps.apv package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CreerPrecompte_QNAME = new QName("http://www.axxx.com/dps/apv", "creerPrecompte");
    private final static QName _CreerPrecompteResponse_QNAME = new QName("http://www.axxx.com/dps/apv", "creerPrecompteResponse");
    private final static QName _PrecomptePartenaire_QNAME = new QName("http://www.axxx.com/dps/apv", "precomptePartenaire");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.axxx.dps.apv
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreerPrecompteResponse }
     * 
     */
    public CreerPrecompteResponse createCreerPrecompteResponse() {
        return new CreerPrecompteResponse();
    }

    /**
     * Create an instance of {@link CreerPrecompte }
     * 
     */
    public CreerPrecompte createCreerPrecompte() {
        return new CreerPrecompte();
    }

    /**
     * Create an instance of {@link PrecomptePartenaire }
     * 
     */
    public PrecomptePartenaire createPrecomptePartenaire() {
        return new PrecomptePartenaire();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreerPrecompte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.axxx.com/dps/apv", name = "creerPrecompte")
    public JAXBElement<CreerPrecompte> createCreerPrecompte(CreerPrecompte value) {
        return new JAXBElement<CreerPrecompte>(_CreerPrecompte_QNAME, CreerPrecompte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreerPrecompteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.axxx.com/dps/apv", name = "creerPrecompteResponse")
    public JAXBElement<CreerPrecompteResponse> createCreerPrecompteResponse(CreerPrecompteResponse value) {
        return new JAXBElement<CreerPrecompteResponse>(_CreerPrecompteResponse_QNAME, CreerPrecompteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrecomptePartenaire }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.axxx.com/dps/apv", name = "precomptePartenaire")
    public JAXBElement<PrecomptePartenaire> createPrecomptePartenaire(PrecomptePartenaire value) {
        return new JAXBElement<PrecomptePartenaire>(_PrecomptePartenaire_QNAME, PrecomptePartenaire.class, null, value);
    }

}
