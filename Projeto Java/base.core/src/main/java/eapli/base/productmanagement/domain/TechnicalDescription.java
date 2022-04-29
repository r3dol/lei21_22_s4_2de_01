package eapli.base.productmanagement.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.util.HashCoder;
import eapli.framework.validations.Preconditions;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TechnicalDescription implements ValueObject, Serializable {

    private final String technicalDescription;


    public TechnicalDescription(final String description) {
        Preconditions.nonEmpty(description);
        this.technicalDescription = description;

    }

    protected TechnicalDescription() {
        this.technicalDescription = "";
    }

    public static TechnicalDescription valueOf(final String description) {
        return new TechnicalDescription(description);
    }


    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof TechnicalDescription)) {
            return false;
        } else {
            TechnicalDescription shortDescription = (TechnicalDescription)o;
            return this.technicalDescription.equals(shortDescription.technicalDescription);
        }
    }

    public int hashCode() {
        HashCoder coder = (new HashCoder()).with(this.technicalDescription);
        return coder.code();
    }

    public String toString() {
        return this.technicalDescription;
    }
}
