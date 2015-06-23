package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContext;

import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.Controllers.MainActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by summer1 on 22/06/2015.
 */
public class WordDataModelTest extends AndroidTestCase{

    private WordDataModel wd;
    private Word w;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        wd = new WordDataModel(context);
        w = new Word();
    }


    @Test
    public void testInsert() throws Exception {
        w.setDictionaryID(1);
        w.setHeadword("Bonjour");
        w.setTranslation("Hello");
        w.setNote("test");
        assertNotNull(w);
        assertNotNull(wd);
        wd.insert(w);
    }

    @Test
    public void testSelectFromID() throws Exception {
        assertNotNull(w);
        Word w2 = wd.selectFromID(w.getId());
        assertEquals(w2.getId(), w.getId());
        assertEquals(w2.getHeadword(), w.getHeadword());
    }

    @Test
    public void testSelectFromHeadWord() throws Exception {

    }

    @Test
    public void testSelectFromWholeWord() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }
}