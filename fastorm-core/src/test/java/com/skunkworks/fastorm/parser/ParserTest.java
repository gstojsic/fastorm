package com.skunkworks.fastorm.parser;

import com.skunkworks.fastorm.parser.query.Query;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import org.skunkworks.fastorm.parser.QueryLexer;
import org.skunkworks.fastorm.parser.QueryParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * stole on 04.02.17.
 */
public class ParserTest {
    private static final Logger l = Logger.getLogger(ParserTest.class.getName());

    @Test
    public void parse() throws Exception {
        //String sql = "findByNameOrLastNameOrderByBanana";
        String sql = "findByNameOrLastNameAndLastName1OrLastName2OrderByBanana";
//        String sql = "findByNameAndLastName";
        final InputStream is = new ByteArrayInputStream(sql.getBytes(Charset.forName("UTF-8")));

        final ANTLRInputStream inputStream = new ANTLRInputStream(is);
        // Create an ExprLexer that feeds from that stream
        final QueryLexer lexer = new QueryLexer(inputStream);
        // Create a stream of tokens fed by the lexer
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Create a parser that feeds off the token stream
        final QueryParser parser = new QueryParser(tokens);
        // Begin parsing at rule query
        final QueryParser.QueryContext queryContext = parser.query();
        Query ctx = queryContext.ctx;
        l.info("Syntax Errors:" + parser.getNumberOfSyntaxErrors());
        l.info("Done:" + queryContext.getText());
    }
}
