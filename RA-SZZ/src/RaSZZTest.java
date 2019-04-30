import static org.junit.Assert.*;

import org.junit.Test;

import br.ufrn.raszz.miner.szz.RaSZZ;
import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.SZZImplementationType;

public class RaSZZTest {

	/*@Test
	public void test1() {
		
		RaSZZ szz = new RaSZZ();		
		RepositoryType repoType = RepositoryType.SVN;
		String[] projects = { "Geronimo" };
		try {
			szz.init(projects, repoType, true, "44680");
		} catch (Exception e) {
			fail("Implementation error");
		}
		
		//fail("Not yet implemented");
		assertTrue(true);
	}
	
	@Test
	public void test2() {
		
		RaSZZ szz = new RaSZZ();		
		RepositoryType repoType = RepositoryType.SVN;
		String[] projects = { "Derby" };
		try {
			String debugRev = "380278";
			String debugPath = "/db/derby/code/trunk/java/drda/org/apache/derby/impl/drda/DRDAStatement.java";
			String debugContent = "		Class[] PREP_STMT_PARAM = { String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE };";
			String[] debugInfos = {debugRev, debugPath, debugContent};			
			szz.init(projects, repoType, true, debugInfos);
		} catch (Exception e) {
			fail("Implementation error");
		}
		
		//fail("Not yet implemented");
		assertTrue(true);
	}
	
	@Test
	public void test3() {
		
		RaSZZ szz = new RaSZZ();		
		RepositoryType repoType = RepositoryType.SVN;
		String[] projects = { "Cmel" };
		try {
			String debugRev = "788943";
			String debugPath = "/camel/trunk/camel-core/src/main/java/org/apache/camel/processor/interceptor/DefaultTraceEventMessage.java";
			String debugContent = "     private String extractFromNode(Exchange exchange) {";
			String[] debugInfos = {debugRev, debugPath, debugContent};			
			szz.init(projects, repoType, true, debugInfos);
		} catch (Exception e) {
			fail("Implementation error");
		}
		
		//fail("Not yet implemented");
		assertTrue(true);
	}
	
	@Test
	public void test4() {
		
		RaSZZ szz = new RaSZZ();		
		RepositoryType repoType = RepositoryType.SVN;
		String[] projects = { "Camel" };
		try {
			String debugRev = "785915";
			String debugPath = "/camel/trunk/components/camel-mail/src/main/java/org/apache/camel/component/mail/MailBinding.java";
			String debugContent = "protectedvoidappendHeadersFromCamelMessage(MimeMessagemimeMessage,Exchangeexchange,org.apache.camel.MessagecamelMessage)throwsMessagingException{";
			String[] debugInfos = {debugRev, debugPath, debugContent};			
			szz.init(projects, repoType, true, debugInfos);
		} catch (Exception e) {
			fail("Implementation error");
		}
		
		//fail("Not yet implemented");
		assertTrue(true);
	}
	
	@Test
	public void test5() {
		
		RaSZZ szz = new RaSZZ();		
		RepositoryType repoType = RepositoryType.SVN;
		String[] projects = { "Pig" };
		try {
			String debugRev = "619213";
			String debugPath = "/incubator/pig/trunk/src/org/apache/pig/backend/local/executionengine/POCogroup.java";
			String debugContent = "-	List<Datum[]>[] sortedInputs;";
			String[] debugInfos = {debugRev, debugPath, debugContent};			
			szz.init(projects, repoType, true, debugInfos);
		} catch (Exception e) {
			fail("Implementation error");
		}
		
		//fail("Not yet implemented");
		assertTrue(true);
	}*/
	
	@Test
	public void test6() {
		
		RaSZZ szz = new RaSZZ();		
		RepositoryType repoType = RepositoryType.SVN;
		String[] projects = { "HBase" };
		try {
			String debugRev = "636849";
			String debugPath = "/hadoop/hbase/branches/0.1/src/java/org/apache/hadoop/hbase/HRegionServer.java";
			String debugContent = "                 case HMsg.MSG_CALL_SERVER_STARTUP:";
			String[] debugInfos = {debugRev, debugPath, debugContent};		
			SZZImplementationType szzType = SZZImplementationType.RASZZ;
			szz.init(projects, repoType, szzType, true, debugInfos);
		} catch (Exception e) {
			fail("Implementation error");
		}
		
		//fail("Not yet implemented");
		assertTrue(true);
	}
	
	
	/*if ((path.equals("/db/derby/code/trunk/java/shared/org/apache/derby/shared/common/reference/SQLState.java") && i == 427899)
    ||(path.equals("/db/derby/code/trunk/java/shared/org/apache/derby/shared/common/reference/SQLState.java") && i == 537850)
    ||(path.equals("/db/derby/code/trunk/java/shared/org/apache/derby/shared/common/reference/SQLState.java") && i == 590046))*/
	/*
	if ((path.equals("/db/derby/code/trunk/java/shared/org/apache/derby/shared/common/reference/SQLState.java")
		||path.equals("/db/derby/code/branches/10.8/java/shared/org/apache/derby/shared/common/reference/SQLState.java")
		|| path.equals("/db/derby/code/branches/10.7/java/shared/org/apache/derby/shared/common/reference/SQLState.java")
		|| path.equals("/db/derby/code/branches/10.6/java/shared/org/apache/derby/shared/common/reference/SQLState.java")
		|| path.equals("/db/derby/code/branches/10.5/java/shared/org/apache/derby/shared/common/reference/SQLState.java")
		|| path.equals("/db/derby/code/branches/10.4/java/shared/org/apache/derby/shared/common/reference/SQLState.java")) &&
		!(i == 405205 || i == 418692 || i == 425446 || i == 430891 || i == 448048 || i == 537850 | i == 547062 || i == 576936
		|| i == 576987 || i == 577791 || i == 620522 || i == 642982 || i == 643861 || i == 769596 
		|| i == 791826 || i == 909633 || i == 1037716 || i == 1037776 || i == 1155332 || i == 1179717
		|| i == 1188109 || i == 1215365 || i == 1327471 || i == 1367150 || i == 1504199)){
		//count++;
		continue;
	}*/
	/*
	if(!path.equals("" +
		"/incubator/activemq/trunk/activemq-core/src/main/java/org/apache/activemq/kaha/impl/container/MapContainerImpl.java"
		//"/activemq/trunk/activemq-core/src/main/java/org/apache/activemq/broker/TransportConnection.java"
		//"/camel/trunk/camel-core/src/main/java/org/apache/camel/util/ObjectHelper.java"
		//"/hadoop/hbase/trunk/src/java/org/apache/hadoop/hbase/regionserver/Store.java"
		//"/tuscany/branches/sca-java-1.3.2/modules/contribution/src/main/java/org/apache/tuscany/sca/contribution/processor/ExtensibleStAXArtifactProcessor.java"
		//"/activemq/camel/trunk/components/camel-spring/src/main/java/org/apache/camel/spring/handler/BeanDefinitionParser.java"
		//"/pig/trunk/src/org/apache/pig/pen/EquivalenceClasses.java"
		//"/hbase/branches/0.95/hbase-protocol/src/main/java/org/apache/hadoop/hbase/protobuf/generated/RPCProtos.java"
		//"/incubator/activemq/trunk/activemq-core/src/main/java/org/activemq/transport/stomp/StompTransportFactory.java"
		))
	continue;/**/
	
	//if (linetotrace.getContent().equals("		Class[] PREP_STMT_PARAM = { String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE };")
	
	
	//if (linetotrace.getContent().equals("staticCollection<IdentityHashSet<Tuple>>getEquivalenceClasses(LOFilterop,Map<LogicalOperator,DataBag>derivedData){")){
	//if (linetotrace.getContent().equals("publicclassExtensibleStAXArtifactProcessorimplementsStAXArtifactProcessor<Object>{")){
	//if(linetotrace.getContent().equals("public class BeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {")){
	/*if(linetotrace.getContent().equals("                    long nextItem=root.getNextItem();")){
		log.info("achou!!");
	}/**/

}
