## TODO

### Dealt with?

```
Empty PSI elements should not be passed to createDescriptor. Start: PsiExpression(empty), end: PsiExpression(empty), startContainingFile: PsiJavaFile:OrderContext.java 
java.lang.Throwable
    at com.intellij.openapi.diagnostic.Logger.error(Logger.java:132)
	at com.intellij.codeInspection.ProblemDescriptorBase.<init>(ProblemDescriptorBase.java:70)
	at com.intellij.codeInspection.InspectionManagerBase.createProblemDescriptor(InspectionManagerBase.java:75)
	at com.intellij.codeInspection.InspectionManagerBase.createProblemDescriptor(InspectionManagerBase.java:64)
	at com.intellij.codeInspection.ProblemsHolder.registerProblem(ProblemsHolder.java:66)
	at com.intellij.codeInspection.ProblemsHolder.registerProblem(ProblemsHolder.java:59)
	at com.lmax.intellijLint.Units.UnitsInspection.reportResolutionFailure(UnitsInspection.java:227)
	at com.lmax.intellijLint.Units.UnitsInspection.reportResolutionFailure(UnitsInspection.java:218)
	at com.lmax.intellijLint.Units.UnitsInspection.inspect(UnitsInspection.java:194)
	at com.lmax.intellijLint.Units.UnitsInspection.access$200(UnitsInspection.java:22)
	at com.lmax.intellijLint.Units.UnitsInspection$1.visitMethodCallExpression(UnitsInspection.java:187)
	at com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl.accept(PsiMethodCallExpressionImpl.java:142)
	at com.intellij.codeInspection.InspectionEngine.acceptElements(InspectionEngine.java:81)
	at com.intellij.codeInspection.InspectionEngine.createVisitorAndAcceptElements(InspectionEngine.java:69)
	at com.intellij.codeInsight.daemon.impl.LocalInspectionsPass.a(LocalInspectionsPass.java:280)
	at com.intellij.codeInsight.daemon.impl.LocalInspectionsPass.a(LocalInspectionsPass.java:249)
	at com.intellij.concurrency.ApplierCompleter.a(ApplierCompleter.java:123)
	at com.intellij.openapi.application.impl.ApplicationImpl.tryRunReadAction(ApplicationImpl.java:1061)
	at com.intellij.concurrency.ApplierCompleter.a(ApplierCompleter.java:96)
	at com.intellij.openapi.progress.impl.CoreProgressManager.a(CoreProgressManager.java:568)
	at com.intellij.openapi.progress.impl.CoreProgressManager.executeProcessUnderProgress(CoreProgressManager.java:519)
	at com.intellij.openapi.progress.impl.ProgressManagerImpl.executeProcessUnderProgress(ProgressManagerImpl.java:54)
	at com.intellij.concurrency.ApplierCompleter.b(ApplierCompleter.java:106)
	at com.intellij.concurrency.ApplierCompleter.compute(ApplierCompleter.java:91)
	at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:731)
	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
```