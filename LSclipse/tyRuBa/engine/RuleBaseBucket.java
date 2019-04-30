/* 
*    Ref-Finder
*    Copyright (C) <2015>  <PLSE_UCLA>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tyRuBa.engine;

import junit.framework.Assert;
import tyRuBa.engine.factbase.ValidatorManager;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public abstract class RuleBaseBucket
  extends QueryEngine
{
  private static int tmpBuckets = 0;
  Validator validator;
  String identifier;
  boolean temporary;
  FrontEnd frontend;
  BucketModedRuleBaseIndex rulebase;
  
  public void setOutdated()
  {
    synchronized (this.frontend)
    {
      this.frontend.getFrontEndValidatorManager().update(this.validator.handle(), new Boolean(true), null);
      this.frontend.someOutdated = true;
    }
  }
  
  public RuleBaseBucket(FrontEnd frontend, String identifyingString)
  {
    this.frontend = frontend;
    Assert.assertTrue(frontend != null);
    if (identifyingString == null)
    {
      this.temporary = true;
      this.identifier = ("TMP_" + tmpBuckets++);
      
      this.validator = frontend.obtainGroupValidator(this.identifier, this.temporary);
    }
    else
    {
      this.temporary = false;
      this.identifier = identifyingString;
      
      this.validator = frontend.obtainGroupValidator(this.identifier, this.temporary);
    }
    frontend.addBucket(this);
    this.rulebase = new BucketModedRuleBaseIndex(this, this.identifier, 
      (BasicModedRuleBaseIndex)frontend.rulebase());
  }
  
  public void insert(RBComponent t)
    throws TypeModeError
  {
    super.insert(new ValidatorComponent(t, this.validator));
  }
  
  public String getStoragePath()
  {
    return this.frontend.getStoragePath() + "/" + this.identifier;
  }
  
  public String getIdentifier()
  {
    return this.identifier;
  }
  
  protected void clear()
  {
    this.validator.invalidate();
    this.frontend.getFrontEndValidatorManager().remove(this.validator.handle());
    this.validator = this.frontend.obtainGroupValidator(this.identifier, this.temporary);
    
    this.rulebase.clear();
    this.frontend.flush();
  }
  
  public FrontEnd frontend()
  {
    return this.frontend;
  }
  
  public boolean isOutdated()
  {
    return this.validator.isOutdated();
  }
  
  public boolean isTemporary()
  {
    return this.temporary;
  }
  
  protected abstract void update()
    throws TypeModeError, ParseException;
  
  void doUpdate()
    throws TypeModeError, ParseException
  {
    update();
    this.frontend.getFrontEndValidatorManager().update(this.validator.handle(), new Boolean(false), null);
  }
  
  ModedRuleBaseIndex rulebase()
  {
    return this.rulebase;
  }
  
  /* Error */
  public void destroy()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 25	tyRuBa/engine/RuleBaseBucket:frontend	LtyRuBa/engine/FrontEnd;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 25	tyRuBa/engine/RuleBaseBucket:frontend	LtyRuBa/engine/FrontEnd;
    //   11: astore_2
    //   12: aload_0
    //   13: getfield 25	tyRuBa/engine/RuleBaseBucket:frontend	LtyRuBa/engine/FrontEnd;
    //   16: invokevirtual 163	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   19: invokevirtual 167	tyRuBa/util/SynchPolicy:stopSources	()V
    //   22: aload_0
    //   23: invokevirtual 172	tyRuBa/engine/RuleBaseBucket:clear	()V
    //   26: aload_0
    //   27: getfield 25	tyRuBa/engine/RuleBaseBucket:frontend	LtyRuBa/engine/FrontEnd;
    //   30: aload_0
    //   31: invokevirtual 173	tyRuBa/engine/FrontEnd:removeBucket	(LtyRuBa/engine/RuleBaseBucket;)V
    //   34: aload_0
    //   35: getfield 69	tyRuBa/engine/RuleBaseBucket:temporary	Z
    //   38: ifeq +19 -> 57
    //   41: new 176	java/io/File
    //   44: dup
    //   45: aload_0
    //   46: invokevirtual 178	tyRuBa/engine/RuleBaseBucket:getStoragePath	()Ljava/lang/String;
    //   49: invokespecial 179	java/io/File:<init>	(Ljava/lang/String;)V
    //   52: astore_3
    //   53: aload_3
    //   54: invokestatic 180	tyRuBa/util/Files:deleteDirectory	(Ljava/io/File;)V
    //   57: aload_0
    //   58: aconst_null
    //   59: putfield 25	tyRuBa/engine/RuleBaseBucket:frontend	LtyRuBa/engine/FrontEnd;
    //   62: aload_0
    //   63: aconst_null
    //   64: putfield 106	tyRuBa/engine/RuleBaseBucket:rulebase	LtyRuBa/engine/BucketModedRuleBaseIndex;
    //   67: aload_0
    //   68: aconst_null
    //   69: putfield 33	tyRuBa/engine/RuleBaseBucket:validator	LtyRuBa/engine/Validator;
    //   72: goto +15 -> 87
    //   75: astore 4
    //   77: aload_2
    //   78: invokevirtual 163	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   81: invokevirtual 186	tyRuBa/util/SynchPolicy:allowSources	()V
    //   84: aload 4
    //   86: athrow
    //   87: aload_2
    //   88: invokevirtual 163	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   91: invokevirtual 186	tyRuBa/util/SynchPolicy:allowSources	()V
    //   94: aload_1
    //   95: monitorexit
    //   96: goto +6 -> 102
    //   99: aload_1
    //   100: monitorexit
    //   101: athrow
    //   102: return
    // Line number table:
    //   Java source line #139	-> byte code offset #0
    //   Java source line #140	-> byte code offset #7
    //   Java source line #141	-> byte code offset #12
    //   Java source line #143	-> byte code offset #22
    //   Java source line #144	-> byte code offset #26
    //   Java source line #146	-> byte code offset #34
    //   Java source line #147	-> byte code offset #41
    //   Java source line #148	-> byte code offset #53
    //   Java source line #150	-> byte code offset #57
    //   Java source line #151	-> byte code offset #62
    //   Java source line #152	-> byte code offset #67
    //   Java source line #154	-> byte code offset #75
    //   Java source line #155	-> byte code offset #77
    //   Java source line #156	-> byte code offset #84
    //   Java source line #155	-> byte code offset #87
    //   Java source line #139	-> byte code offset #94
    //   Java source line #158	-> byte code offset #102
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	RuleBaseBucket
    //   5	95	1	Ljava/lang/Object;	Object
    //   11	77	2	holdOn	FrontEnd
    //   52	2	3	f	java.io.File
    //   75	10	4	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   22	75	75	finally
    //   7	96	99	finally
    //   99	101	99	finally
  }
  
  public void backup()
  {
    this.rulebase.backup();
  }
  
  public void enableMetaData()
  {
    this.rulebase.enableMetaData();
  }
}
