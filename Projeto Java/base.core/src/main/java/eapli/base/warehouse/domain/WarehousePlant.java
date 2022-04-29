package eapli.base.warehouse.domain;

import eapli.framework.domain.model.AggregateRoot;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;

import java.util.List;

@Entity
public class WarehousePlant implements AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String description;

    private int length;

    private int width;

    private int square;

    private String unit;

    public WarehousePlant(){}

    public WarehousePlant(final String description, final int length, final int width, final int square, final String unit) {
        this.description = description;
        this.length = length;
        this.width = width;
        this.square = square;
        this.unit = unit;
    }

    @Override
    public boolean sameAs(Object other) {
        if(other == null) return false;
        if(this == other) return true;

        WarehousePlant newObj = ((WarehousePlant) other);

        return description == newObj.description && length == newObj.length && width == newObj.width && square == newObj.square
                && unit == newObj.unit;
    }

    @Override
    public Long identity() {
        return Id;
    }
}
