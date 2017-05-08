package com.samsungsds.analyst.code.jdepend.framework;

import java.util.*;

import com.samsungsds.analyst.code.jdepend.framework.JavaPackage;

/**
 * The <code>DependencyConstraint</code> class is a constraint that tests
 * whether two package-dependency graphs are equivalent.
 * <p>
 * This class is useful for writing package dependency assertions (e.g. JUnit).
 * For example, the following JUnit test will ensure that the 'ejb' and 'web'
 * packages only depend upon the 'util' package, and no others:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * public void testDependencyConstraint() {
 * 
 *     JDepend jdepend = new JDepend();
 *     jdepend.addDirectory(&quot;/path/to/classes&quot;);
 *     Collection analyzedPackages = jdepend.analyze();
 * 
 *     DependencyConstraint constraint = new DependencyConstraint();
 * 
 *     JavaPackage ejb = constraint.addPackage(&quot;com.xyz.ejb&quot;);
 *     JavaPackage web = constraint.addPackage(&quot;com.xyz.web&quot;);
 *     JavaPackage util = constraint.addPackage(&quot;com.xyz.util&quot;);
 * 
 *     ejb.dependsUpon(util);
 *     web.dependsUpon(util);
 * 
 *     assertEquals(&quot;Dependency mismatch&quot;, true, constraint
 *             .match(analyzedPackages));
 * }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author <b>Mike Clark</b> 
 * @author Clarkware Consulting, Inc.
 */

public class DependencyConstraint {

    private HashMap<String, JavaPackage> packages;

    public DependencyConstraint() {
        packages = new HashMap<>();
    }

    public JavaPackage addPackage(String packageName) {
        JavaPackage jPackage = (JavaPackage) packages.get(packageName);
        if (jPackage == null) {
            jPackage = new JavaPackage(packageName);
            addPackage(jPackage);
        }
        return jPackage;
    }

    public void addPackage(JavaPackage jPackage) {
        if (!packages.containsValue(jPackage)) {
            packages.put(jPackage.getName(), jPackage);
        }
    }

    public Collection<JavaPackage> getPackages() {
        return packages.values();
    }

    /**
     * Indicates whether the specified packages match the 
     * packages in this constraint.
     * 
     * @return <code>true</code> if the packages match this constraint
     */
    public boolean match(Collection<JavaPackage> expectedPackages) {

        if (packages.size() == expectedPackages.size()) {
            
            for (JavaPackage nextPackage : expectedPackages) {
                if (!matchPackage(nextPackage)) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    private boolean matchPackage(JavaPackage expectedPackage) {

        JavaPackage actualPackage = (JavaPackage) packages.get(expectedPackage.getName());

        if (actualPackage != null) {
            if (equalsDependencies(actualPackage, expectedPackage)) {
                return true;
            }
        }

        return false;
    }

    private boolean equalsDependencies(JavaPackage a, JavaPackage b) {
        return equalsAfferents(a, b) && equalsEfferents(a, b);
    }

    private boolean equalsAfferents(JavaPackage a, JavaPackage b) {

        if (a.equals(b)) {

            Collection<JavaPackage> otherAfferents = b.getAfferents();

            if (a.getAfferents().size() == otherAfferents.size()) {
            	for (JavaPackage afferent : a.getAfferents()) {
                    if (!otherAfferents.contains(afferent)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private boolean equalsEfferents(JavaPackage a, JavaPackage b) {

        if (a.equals(b)) {

            Collection<JavaPackage> otherEfferents = b.getEfferents();

            if (a.getEfferents().size() == otherEfferents.size()) {
            	
            	for (JavaPackage efferent : a.getEfferents()) {
                    if (!otherEfferents.contains(efferent)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
}