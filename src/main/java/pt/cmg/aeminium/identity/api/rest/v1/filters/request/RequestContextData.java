/**
 * Copyright (c) 2020 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.filters.request;

import java.io.Serializable;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;

/**
 * This object will hold Request data that is filled in every request.
 * It is contextual data like the expected response language, the device type that performed the request
 * or any other kind of volatile data that is somehow relevant to the current request.
 * <br>
 * It is supposed to be filled before the request hits the resource function by a chain of filters.
 *
 * @author Carlos Gonçalves
 */
public class RequestContextData implements Serializable {

    private static final long serialVersionUID = 2643263275423254609L;

    private Language selectedLanguage;
    private ClientApplicationData appData;

    // There is no reason to inject a User because WELD will just inject a proxy, which will break when I try to do something
    // related to JPA with it (such as persisting it). It is better to inject the user id and then use a DAO to load it if needed.
    // It is cached anyway so there really is no penalty other than difficulty in reading the code
    private Long userId;

    // NOTE: this needs getters and setters because it injects WELD proxies, so without getters and setters there is no way to access these variables
    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public ClientApplicationData getAppData() {
        return appData;
    }

    public void setAppData(ClientApplicationData appData) {
        this.appData = appData;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
