package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "functional_api_testing")
public class FunctionalApiTesting extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "endpoint_url")
    private String endpointUrl;
    
    @Column(name = "http_method")
    private String httpMethod;
    
    @Column(name = "request_body", columnDefinition = "jsonb")
    private String requestBody;
    
    @Column(name = "expected_response", columnDefinition = "jsonb")
    private String expectedResponse;}