package idea.plugins.thirdparty.filecompletion.jrr.a.file

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.progress.ProgressManager
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

/**
 * Calculate proposals for file
 *
 */
@CompileStatic
public class FileProposalCalculator {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    private static final java.util.logging.Logger LOG = log


    static List<File> calculateProposals(
            final String stringLiteral, final int documentOffset, File parentFile, CompletionResultSet cr) {
        List<File> listOfStrings = calculateProposalsImpl(stringLiteral, documentOffset, parentFile, cr);
        return listOfStrings;
    }

    static List<File> calculateProposalsImpl(
            final String stringLiteral, final int documentOffset, File parentFile, CompletionResultSet cr)
            throws IOException {
        ProgressManager.checkCanceled();
        List<File> proposals = new ArrayList<File>();
        if (stringLiteral.trim().length() == 0) {
            proposals = calculateProposalsForEmptyString(parentFile, cr);
        } else {
            int fromStart = documentOffset;
            String pathFromStartLiteralToCursor = stringLiteral.substring(0, documentOffset);
            pathFromStartLiteralToCursor = pathFromStartLiteralToCursor.replace("\"", "").replace("\\\\", "/");
            log.debug(pathFromStartLiteralToCursor);
            if (pathFromStartLiteralToCursor.trim().length() == 0) {
                proposals = calculateProposalsForEmptyString(parentFile, cr);
            } else {
                String pathFromDocumentOfficetToEnd = stringLiteral.substring(fromStart);
                log.debug("pathFromDocumentOfficetToEnd cp2 " + pathFromDocumentOfficetToEnd);
                pathFromDocumentOfficetToEnd = pathFromDocumentOfficetToEnd.replace("\"", "").replace("\\\\", "/");
                int nextShalsh = pathFromDocumentOfficetToEnd.indexOf("/");
                log.debug("pathFromStartLiteralToCursor = ${pathFromStartLiteralToCursor}")
                File file
                if (parentFile == null) {
                    file = new File(pathFromStartLiteralToCursor);
                } else {
                    file = new File(parentFile, pathFromStartLiteralToCursor);
                }
                if (pathFromStartLiteralToCursor.endsWith("/")) {
                    if (file.exists() && file.isDirectory()) {
                        File[] files = file.listFiles();
                        if (files == null) {
                            log.debug("can't list files");
                        } else {
                            log.debug("calc calculateProposalsWithSlash")
                            proposals = calculateProposalsWithSlash(stringLiteral, documentOffset, files,
                                    nextShalsh, cr);
                        }
                    }
                } else {
                    int lastShalsh = pathFromStartLiteralToCursor.lastIndexOf('/');
                    // want to get rid just of string
                    if (lastShalsh == -1) {
                        if (parentFile == null) {
                            proposals = calculateProposalsForEmptyString(null, cr);
                        } else {
                            String rest = pathFromStartLiteralToCursor;
                            log.debug("rest = " + rest);
                            File file2 = parentFile;
                            log.debug("abs path " + file2.getAbsolutePath());
                            if (file2.exists() && file2.isDirectory()) {
                                log.debug("calc NotEndSlash")
                                proposals = calculateProposalsWithNotEndSlash(stringLiteral, documentOffset,
                                        file2.listFiles(), nextShalsh, rest, cr);
                            } else {
                                log.debug("not exists " + file2);
                            }
                        }
                    } else {
                        String dirrr = pathFromStartLiteralToCursor.substring(0, lastShalsh + 1);
                        String rest = pathFromStartLiteralToCursor.substring(lastShalsh + 1).toLowerCase();
                        log.debug("dirr = " + dirrr);
                        log.debug("rest = " + rest);
                        File file2;
                        if (parentFile == null) {
                            file2 = new File(dirrr);
                        } else {
                            file2 = new File(parentFile, dirrr);
                        }
                        log.debug("abs path " + file2.getAbsolutePath());
                        if (file2.exists() && file2.isDirectory()) {
                            log.debug("calc NotEndSlash")
                            proposals = calculateProposalsWithNotEndSlash(stringLiteral, documentOffset,
                                    file2.listFiles(), nextShalsh, rest, cr);
                        } else {
                            log.debug("not exists " + file2);
                        }
                    }
                }
            }
        }
        log.debug("proposal count " + proposals.size());
        Collections.sort(proposals);

        return proposals;
    }

    static List<File> calculateProposalsForEmptyString(File parentFile, CompletionResultSet cr) throws IOException {
        ProgressManager.checkCanceled();
        List<File> sss = new ArrayList<File>();
        File[] listRoots
        if (parentFile == null) {
            listRoots = File.listRoots();
        } else {
            if (!parentFile.exists()) {
                log.debug("parent not exist ${parentFile}")
                return sss;
            }
            listRoots = parentFile.listFiles();
        }
        for (File file2 : listRoots) {
            if (cr.isStopped()) {
                return sss
            }
            ProgressManager.checkCanceled();
            long startInLoop = System.currentTimeMillis();
            sss.add(file2);
            startInLoop = System.currentTimeMillis() - startInLoop;
            startInLoop = startInLoop.intdiv(1000) as Long;
            if (startInLoop > 2) {
                log.debug("listing take too much time " + startInLoop + " " + file2.getAbsolutePath());
            }
        }

        sss = sss.sort()
        log.debug("returning roots " + sss);
        return sss;
    }

    static List<File> calculateProposalsWithNotEndSlash(final String stringLiteral,
                                                        final int documentOffset, File[] files, int nextShalsh, String rest, CompletionResultSet cr) {
        ProgressManager.checkCanceled();
        rest = rest.toLowerCase()
        //String absPath = parentFile.absolutePath.tr('\\', '/') + '/'
        List<File> sss = new ArrayList<File>();
        for (File file3 : files) {
            if (cr.isStopped()) {
                return sss
            }
            ProgressManager.checkCanceled();
            long startInLoop = System.currentTimeMillis();
            String fileName = file3.name;
            // log.debug "${fileName} , rest = ${rest}"
            if (fileName.toLowerCase().startsWith(rest)) {
                sss.add(file3);
            }
            startInLoop = System.currentTimeMillis() - startInLoop;
            startInLoop = startInLoop.intdiv(1000) as Long;
            if (startInLoop > 2) {
                log.debug("Listing take too much time " + startInLoop + " " + file3.getAbsolutePath());
            }
        }
        return sss;

    }

    static List<File> calculateProposalsWithSlash(final String stringLiteral,
                                                  final int documentOffset, File[] files, int nextShalsh, CompletionResultSet cr) {
        ProgressManager.checkCanceled();
        log.debug("${files.size()}");
        List<File> sss = new ArrayList<File>();
        for (File file2 : files) {
            if (cr.isStopped()) {
                return sss
            }
            ProgressManager.checkCanceled();
            long startInLoop = System.currentTimeMillis();
            sss.add(file2);
//			sss.add(new CompletionProposal(string, documentOffset, replacementLength, string.length()));
            startInLoop = System.currentTimeMillis() - startInLoop;
            startInLoop = startInLoop.intdiv(1000) as Long;
            if (startInLoop > 2) {
                log.debug("listing take too much time " + startInLoop + " " + file2.getAbsolutePath());
            }
        }
        return sss;
    }

}
