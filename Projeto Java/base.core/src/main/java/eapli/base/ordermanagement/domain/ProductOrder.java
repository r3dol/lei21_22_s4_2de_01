package eapli.base.ordermanagement.domain;

import eapli.base.clientmanagement.domain.Client;
import eapli.base.productmanagement.application.ListProductService;
import eapli.base.productmanagement.domain.Product;
import eapli.base.productmanagement.domain.UniqueInternalCode;
import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;
import eapli.framework.general.domain.model.Money;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import eapli.framework.time.util.Calendars;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class ProductOrder implements AggregateRoot<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    public enum SourceChannel {
        CALL, EMAIL, MEETING;
    }

    public enum Payment {
        PAYPAL, APPLE_PAY;
    }

    @Version
    private Long version;

    @Id
    @GeneratedValue
    private Long orderId;

    @ManyToOne
    private Client client;

    @Temporal(TemporalType.DATE)
    private Calendar createdOn;

    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetName", column = @Column(name = "billing_streetName")),
            @AttributeOverride(name = "doorNumber", column = @Column(name = "billing_doorNumber")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postalCode")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private Address billingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetName", column = @Column(name = "shipping_streetName")),
            @AttributeOverride(name = "doorNumber", column = @Column(name = "shipping_doorNumber")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postalCode")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country"))
    })
    private Address shippingAddress;

    @ElementCollection
    private Set<OrderItem> items;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "no_taxes_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "no_taxes_currency"))
    })
    private Money totalAmountWithoutTaxes;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "taxes_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "taxes_currency"))
    })
    private Money totalAmountWithTaxes;

    @Enumerated(EnumType.STRING)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    private Payment payment;

    private OrderVolume orderVolume;

    private OrderWeight orderWeight;

    @Enumerated(EnumType.STRING)
    private SourceChannel sourceChannel;

    @Temporal(TemporalType.DATE)
    private Calendar interactionDate;

    private AdditionalComment additionalComment;

    @ManyToOne
    private SystemUser salesClerk;

    public ProductOrder(final Client client, final Address billingAddress, final Address shippingAddress, final Shipment shipment, final SourceChannel sourceChannel, final Calendar interactionDate, final AdditionalComment additionalComment, final SystemUser salesClerk, final Set<OrderItem> orderItems, final Payment payment) {
        this.createdOn = Calendars.now();
        this.client = client;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.items = orderItems;
        this.shipment = shipment;
        this.payment = payment;
        this.sourceChannel = sourceChannel;
        this.interactionDate = interactionDate;
        this.additionalComment = additionalComment;
        this.salesClerk = salesClerk;
        this.totalAmountWithoutTaxes = obtainTotalAmountWithoutTaxes(new ListProductService());
        this.totalAmountWithTaxes = obtainTotalAmountWithTaxes(new ListProductService());
        this.orderWeight = obtainTotalOrderWeight(new ListProductService());
        this.orderVolume = obtainTotalOrderVolume(new ListProductService());
        this.status = new OrderStatus(OrderStatus.Status.TO_BE_PREPARED);

    }

    public ProductOrder(final Client client, final Address billingAddress, final Address shippingAddress, final Shipment shipment, final SourceChannel sourceChannel, final Calendar interactionDate, final SystemUser salesClerk, final Set<OrderItem> orderItems, final Payment payment) {
        this.createdOn = Calendars.now();
        this.client = client;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.items = orderItems;
        this.shipment = shipment;
        this.payment = payment;
        this.sourceChannel = sourceChannel;
        this.interactionDate = interactionDate;
        this.salesClerk = salesClerk;
        this.totalAmountWithoutTaxes = obtainTotalAmountWithoutTaxes(new ListProductService());
        this.totalAmountWithTaxes = obtainTotalAmountWithTaxes(new ListProductService());
        this.orderWeight = obtainTotalOrderWeight(new ListProductService());
        this.orderVolume = obtainTotalOrderVolume(new ListProductService());
        this.status = new OrderStatus(OrderStatus.Status.TO_BE_PREPARED);
    }

    public Money obtainTotalAmountWithoutTaxes(ListProductService svcProducts) {
        double totalAmountWithoutTaxes = 0;

        for (OrderItem orderItem : items) {
            String code = orderItem.code();
            Product product = svcProducts.findProductByUniqueInternalCode(new UniqueInternalCode(code));

            totalAmountWithoutTaxes += (orderItem.quantity() * product.getPriceWithoutTaxes().amountAsDouble());

        }
        return this.totalAmountWithoutTaxes = Money.euros(totalAmountWithoutTaxes);
    }

    public Money obtainTotalAmountWithTaxes(ListProductService svcProducts) {
        double totalAmountWithTaxes = 0;

        for (OrderItem orderItem : items) {
            String code = orderItem.code();
            Product product = svcProducts.findProductByUniqueInternalCode(new UniqueInternalCode(code));
            totalAmountWithTaxes += (orderItem.quantity() * product.getPriceWithTaxes().amountAsDouble());
        }

        return this.totalAmountWithTaxes = Money.euros(totalAmountWithTaxes + this.shipment.cost());
    }

    public OrderWeight obtainTotalOrderWeight(ListProductService svcProducts) {
        long totalWeight = 0;

        for (OrderItem orderItem : items) {
            String code = orderItem.code();
            Product product = svcProducts.findProductByUniqueInternalCode(new UniqueInternalCode(code));

            totalWeight += (orderItem.quantity() * product.getWeight());

        }
        return this.orderWeight = new OrderWeight(totalWeight);
    }

    public OrderVolume obtainTotalOrderVolume(ListProductService svcProducts) {
        long totalVolume = 0;

        for (OrderItem orderItem : items) {
            String code = orderItem.code();
            Product product = svcProducts.findProductByUniqueInternalCode(new UniqueInternalCode(code));

            totalVolume += (orderItem.quantity() * product.getVolume());

        }
        return this.orderVolume = new OrderVolume(totalVolume);
    }

    protected ProductOrder() {
        //for ORM purposes
    }

    @Override
    public int hashCode() {
        return DomainEntities.hashCode(this);
    }

    @Override
    public boolean sameAs(Object other) {
        return DomainEntities.areEqual(this, other);
    }

    @Override
    public Long identity() {
        return this.orderId;
    }




}
