/******************************************************************
 * File:        GoalState.java
 * Created by:  Dave Reynolds
 * Created on:  04-May-2003
 * 
 * (c) Copyright 2003, Hewlett-Packard Company, all rights reserved.
 * [See end of file]
 * $Id: GoalState.java,v 1.1 2003-05-05 15:15:59 der Exp $
 *****************************************************************/
package com.hp.hpl.jena.reasoner.rulesys.impl;

import com.hp.hpl.jena.util.iterator.ClosableIterator;

/**
 * Represents the state in a traversal of all the solutions of a
 * given goal. This will traverse, in turn, all the matches in the
 * underlying triple stores, all the memoized results currently in
 * the GoalTable and then all the additional results which can be
 * found by turns of the goal Generator crank, until the goal (or the
 * whole derivation) is complete.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.1 $ on $Date: 2003-05-05 15:15:59 $
 */
public class GoalState {
    
    /** An iterator over the matching triples in the stores */
    protected ClosableIterator tripleMatches;
    
    /** The goal table entry for this goal */
    protected GoalResults results;
    
    /** The index of the next memoized solution to return */
    protected int solutionPointer = 0;
    
    /**
     * Constructor. Create a GoalState which can traverse all the
     * matches to a goal over a set of raw data plus derivations.
     * @param tripleMatches an interation over the raw data results
     * @param results a GoalResults which gives access to memoized
     * results for this goal together with the Generator that can
     * produce additional results 
     */
    public GoalState(ClosableIterator tripleMatches, GoalResults results) {
        this.tripleMatches = tripleMatches;
        this.results = results;
    }

    /**
     * Return the next available result for this goal.
     * @return a Triple matching the goal if there is another result available, 
     * or FAIL if there are known to be no more matches or SUSPEND if there 
     * may be more results in the future but current progress is blocked waiting
     * for other subgoals.
     */
    public Object next() {
        if (tripleMatches != null) {
            if (tripleMatches.hasNext()) {
                return tripleMatches.next(); 
            } else {
                tripleMatches = null;
            }
        }
        if (solutionPointer < results.numResults()) {
            return results.getResult(solutionPointer++);
        } else {
            return results.crank();
        }
    }
    
    /**
     * Close the GoalState, closing any still active iterators.
     */
    public void close() {
        if (tripleMatches != null) {
            tripleMatches.close();
        }
    }
}



/*
    (c) Copyright Hewlett-Packard Company 2003
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/