//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCutProc N.M.Collins 17/10/01

//24/11/01 added newPhraseAccounting, endBlockAccounting functions to avoid code reuse
//phraselength taken on by base class- no problems since all these responsibilities can still be taken
//on explicitly by derived classes

//making cuts [delta,duration] (notes 10/11/01) would only take minor corrections,
//in the procs and in BBCut main class. No procedure uses this currently and
//unlikely since any constant/proportional duty cycle is already possible with standard enveloping
//in future may want systematic control- ultimately may want [delta,dur,amp,offset] procedural control

//base class is also the NoCutProc (or the OneCutProc= whole phrase)

BBCutProc
{
    var <phrase,<block,<totalbeatsdone;
    var <cuts,<blocklength,<phrasepos,<beatspersubdiv;
    var <currphraselength,<phraselength;
    var bbcutsynth;	//has to call updatephrase, updateblock from here

    *new
    {
        arg bpsd=0.5,phraselength=4.0;

        ^super.new.initBBCutProc(bpsd,phraselength)
    }

    initBBCutProc
    {
        arg bpsd=0.5,pl=4.0;	//default to quavers

        phrase=0;
        block=0;
        beatspersubdiv=bpsd;
        phraselength=pl;
        phrasepos=0.0;
        //must start 0.0 so to trigger a new phrase immediately
        currphraselength=0.0;
        totalbeatsdone=0.0;
    }

    chooseblock
    {
        this.newPhraseAccounting;

        //each phrase has one block
        blocklength=currphraselength;
        cuts=[blocklength];

        //offsets are now decided by cut renderer
        bbcutsynth.chooseoffset(phrasepos,beatspersubdiv,currphraselength);

        this.updateblock;
        this.endBlockAccounting;
    }

    //don't call this.updateblock if infinite phrases
    //just do call yourself as bbcutsynth.updateblock(block,0.0,cuts,0);
    //since phrasepos/currphraselength is always zero for infinite phrases
    updateblock
    {
        arg isroll=0;

        bbcutsynth.updateblock(block,phrasepos/currphraselength,cuts,isroll);
    }

    //will only attach relevant synth engine, in some special cases
    //might need to attach a reference to a list- make a list class?
    //done now via base
    attachsynth
    {
        arg bbcs;
        bbcutsynth=bbcs;
    }

    phraseover
    {
        ^if(((currphraselength-phrasepos)<0.00001),1,0)
    }

    //avoids repeated common code

    //done manually in MultiProc to avoid updatephrase call
    //currphraselength recalculated in BBCutProc11 and may be for future procedures
    newPhraseAccounting
    {
        arg cpl;
        //if nil nothing was passed in; o/w, for procedures that work out the new phrase length
        //themselves can pass in chosen phraselength

        currphraselength=cpl ? (phraselength.value(phrase));
        bbcutsynth.updatephrase(phrase, currphraselength);

        phrasepos=0.0;
        phrase=phrase+1;
        block=0;
    }

    endBlockAccounting
    {
        phrasepos=phrasepos+blocklength;
        totalbeatsdone=totalbeatsdone+ blocklength;
        block=block+1;
    }



}








