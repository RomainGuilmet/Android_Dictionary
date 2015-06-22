package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.Context;
import android.test.mock.MockContext;

import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.Controllers.MainActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by summer1 on 22/06/2015.
 */
public class WordDataModelTest {

    @Test
    public void testInsert() throws Exception {
        Word w = new Word();
        WordDataModel wd = new WordDataModel(MainActivity.getAppContext());
        w.setDictionaryID(1);
        w.setHeadword("Bonjour");
        w.setTranslation("Hello");
        w.setNote("test");
        assertNotNull(w);
        wd.insert(w);
    }

    @Test
    public void testSelectFromID() throws Exception {
        WordDataModel wd = new WordDataModel(MainActivity.getAppContext());
        Word w2 = wd.selectFromID(1);
        assertEquals(w2.getId(), 1);
        assertEquals(w2.getHeadword(), "Bonjour");
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