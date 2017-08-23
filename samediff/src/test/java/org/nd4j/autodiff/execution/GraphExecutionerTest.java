package org.nd4j.autodiff.execution;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.autodiff.execution.conf.ExecutorConfiguration;
import org.nd4j.autodiff.execution.conf.OutputMode;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.autodiff.samediff.impl.SDVariable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.*;

/**
 * Comparative tests for native executioner vs sequential execution
 * @author raver119@gmail.com
 */
@Slf4j
public class GraphExecutionerTest {
    protected static ExecutorConfiguration configVarSpace = ExecutorConfiguration.builder().outputMode(OutputMode.VARIABLE_SPACE).build();
    protected static ExecutorConfiguration configExplicit = ExecutorConfiguration.builder().outputMode(OutputMode.EXPLICIT).build();
    protected static ExecutorConfiguration configImplicit = ExecutorConfiguration.builder().outputMode(OutputMode.IMPLICIT).build();

    @Before
    public void setUp() throws Exception {
        //
    }


    @Test
    public void testEquality1() throws Exception {
        GraphExecutioner executionerA = new BasicGraphExecutioner();
        GraphExecutioner executionerB = new NativeGraphExecutioner();

        SameDiff sameDiff = SameDiff.create();
        INDArray ones = Nd4j.ones(4);
        SDVariable sdVariable = sameDiff.var("ones",ones);
        SDVariable scalarOne = sameDiff.var("add1",Nd4j.scalar(1.0));
        SDVariable result = sdVariable.addi(scalarOne);
        SDVariable total = sameDiff.sum(result,Integer.MAX_VALUE);

        log.info("ID: {}",sameDiff.getGraph().getVertex(1).getValue().getId());

        INDArray[] resB = executionerB.executeGraph(sameDiff, configImplicit);

        assertEquals(1, resB.length);
        assertEquals(Nd4j.scalar(8.0), resB[0]);

        //INDArray resA = executionerA.executeGraph(sameDiff)[0];

        //assertEquals(resA, resB);
    }
}