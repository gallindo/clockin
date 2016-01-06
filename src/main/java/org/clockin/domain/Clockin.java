package org.clockin.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.clockin.domain.enumeration.RegistryType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * A Clockin.
 */
@Entity
@Table(name = "clockin")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "clockin")
public class Clockin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sequential_register_number")
    private String sequentialRegisterNumber;

    @Column(name = "date_time")
    private ZonedDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "registry_type")
    private RegistryType registryType;

    @ManyToOne
    private Employee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSequentialRegisterNumber() {
        return sequentialRegisterNumber;
    }

    public void setSequentialRegisterNumber(String sequentialRegisterNumber) {
        this.sequentialRegisterNumber = sequentialRegisterNumber;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public RegistryType getRegistryType() {
        return registryType;
    }

    public void setRegistryType(RegistryType registryType) {
        this.registryType = registryType;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

	public LocalDate getDate() {

		return dateTime.toLocalDate();
	}

	public LocalTime getTime() {

		return dateTime.toLocalTime();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Clockin clockin = (Clockin) o;

        if ( ! Objects.equals(id, clockin.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Clockin{" +
            "id=" + id +
            ", sequentialRegisterNumber='" + sequentialRegisterNumber + "'" +
            ", dateTime='" + dateTime + "'" +
            ", registryType='" + registryType + "'" +
            '}';
    }
}
