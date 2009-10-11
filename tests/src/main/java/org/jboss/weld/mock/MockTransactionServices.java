/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.weld.mock;

import javax.transaction.Synchronization;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;

/**
 * A mock version of TransactionServices for RI unit tests.  Since
 * no JTA transaction can be active for these unit tests, all
 * methods here are empty.
 * 
 * @author David Allen
 *
 */
public class MockTransactionServices implements TransactionServices
{

   public boolean isTransactionActive()
   {
      return false;
   }

   public void registerSynchronization(Synchronization synchronizedObserver)
   {
   }
   
   public UserTransaction getUserTransaction()
   {
      return null;
   }
   
   public void cleanup() {}

}