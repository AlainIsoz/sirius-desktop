/*******************************************************************************
 * Copyright (c) 2018 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.services.graphql.internal.schema.query.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sirius.services.graphql.internal.SiriusGraphQLMessages;
import org.eclipse.sirius.services.graphql.internal.entities.SiriusGraphQLConnection;
import org.eclipse.sirius.services.graphql.internal.schema.directives.SiriusGraphQLCostDirective;
import org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLPaginationArguments;
import org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLPaginationDataFetcher;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLTypeReference;

/**
 * The ePackages field of the user.
 *
 * @author sbegaudeau
 */
public final class SiriusGraphQLUserEPackagesField {
    /**
     * The name of the field ePackages.
     */
    private static final String EPACKAGES_FIELD = "ePackages"; //$NON-NLS-1$

    /**
     * The name of the includeDefaultEPackages argument.
     */
    private static final String INCLUDE_DEFAULT_EPACKAGES_ARG = "includeDefaultEPackages"; //$NON-NLS-1$

    /**
     * The complexity of the retrieval of an EPackage.
     */
    private static final int COMPLEXITY = 1;

    /**
     * The separator used by the default EPackages.
     */
    private static final String DEFAULT_METAMODELS_SEPARATOR = ","; //$NON-NLS-1$

    /**
     * The constructor.
     */
    private SiriusGraphQLUserEPackagesField() {
        // Prevent instantiation
    }

    /**
     * Returns the ePackages field.
     *
     * @return The ePackages field
     */
    public static GraphQLFieldDefinition build() {
        List<String> multipliers = new ArrayList<>();
        multipliers.add(SiriusGraphQLPaginationArguments.FIRST_ARG);
        multipliers.add(SiriusGraphQLPaginationArguments.LAST_ARG);

        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(EPACKAGES_FIELD)
                .type(new GraphQLTypeReference(SiriusGraphQLUserTypesBuilder.USER_EPACKAGE_CONNECTION_TYPE))
                .argument(SiriusGraphQLPaginationArguments.build())
                .argument(SiriusGraphQLUserEPackagesField.getIncludeDefaultEPackagesArg())
                .withDirective(new SiriusGraphQLCostDirective(COMPLEXITY, multipliers).build())
                .dataFetcher(SiriusGraphQLUserEPackagesField.getEPackagesDataFetcher())
                .build();
        // @formatter:on
    }

    /**
     * Returns the includeDefaultEPackages argument.
     *
     * @return The includeDefaultEPackages argument
     */
    private static GraphQLArgument getIncludeDefaultEPackagesArg() {
        // @formatter:off
        return GraphQLArgument.newArgument()
                .name(INCLUDE_DEFAULT_EPACKAGES_ARG)
                .type(Scalars.GraphQLBoolean)
                .build();
        // @formatter:on
    }

    /**
     * Returns the ePackages data fetcher.
     *
     * @return The ePackages data fetcher.
     */
    private static DataFetcher<SiriusGraphQLConnection> getEPackagesDataFetcher() {
        return SiriusGraphQLPaginationDataFetcher.build(environment -> {
            // @formatter:off
            return EPackage.Registry.INSTANCE.values().stream()
                    .map(object -> {
                        EPackage ePackage = null;
                        if (object instanceof EPackage) {
                            ePackage = (EPackage) object;
                        } else if (object instanceof EPackage.Descriptor) {
                            ePackage = ((EPackage.Descriptor) object).getEPackage();
                        }
                        return ePackage;
                    })
                    .filter(ePackage -> SiriusGraphQLUserEPackagesField.filter(environment, ePackage))
                    .collect(Collectors.toList());
            // @formatter:on
        });
    }

    /**
     * Indicates if the given EPackage should be filtered.
     *
     * @param environment
     *            The data fetching environment
     * @param ePackage
     *            The EPackage to filter
     * @return <code>true</code> if the given EPackage should be kept, <code>false</code> otherwise
     */
    private static boolean filter(DataFetchingEnvironment environment, EPackage ePackage) {
        // @formatter:off
        boolean includeDefaultEPackages = Optional.ofNullable(environment.getArgument(INCLUDE_DEFAULT_EPACKAGES_ARG))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .orElse(Boolean.FALSE)
                .booleanValue();
        // @formatter:on

        String[] defaultMetamodels = SiriusGraphQLMessages.SiriusGraphQLUserEPackagesField_defaultMetamodels.split(DEFAULT_METAMODELS_SEPARATOR);
        return includeDefaultEPackages || !Arrays.asList(defaultMetamodels).contains(ePackage.getNsURI());
    }
}
