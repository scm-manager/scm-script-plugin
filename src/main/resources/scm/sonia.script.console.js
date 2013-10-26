/*
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */

Ext.ns('Sonia.script');

Sonia.script.Console = Ext.extend(Ext.Panel, {
  
  title: 'Console',
  
  editorPanel: null,
  outputPanel: null,
  typeCombobox: null,
  typeStore: null,
  
  script: null,
  
  initComponent: function(){
    this.typeStore = Sonia.script.createTypeStore();
    if ( this.script ){
      this.typeStore.on('load', this.switchEditorMode, this);
    }
    
    this.editorPanel = new Sonia.panel.CodeEditorPanel({
      region: 'center',
      layout: 'fit',
      listeners: {
        editorRendered: {
          fn: function(){
            if (this.script){
              this.loadScriptContent();
            }
          },
          scope: this
        }
      }
    });
    
    this.outputPanel = new Ext.Panel({
      region: 'south',
      layout: 'fit',
      height: 250,
      padding: 5,
      split: true,
      autoScroll: true,
      bodyCssClass: 'x-panel-mc'
    });

    var config = {
      title: this.title,
      layout: 'border',
      tbar: [{
        xtype: 'tbbutton',
        text: 'Execute',
        handler: this.execute,
        scope: this
      },'-',{
        xtype: 'label',
        text: 'Type: ',
        cls: 'ytb-text'
      },'  ',{
        id: 'typeCombobox',
        xtype: 'combo',
        name: 'type',
        triggerAction: 'all',
        editable: false,
        displayField: 'name',
        valueField: 'name',
        value: 'Groovy',
        store: this.typeStore,
        listeners: {
          select: {
            fn: this.changeScriptLanguage,
            scope: this
          }
        }
      }],
      items: [this.editorPanel, this.outputPanel]
    };
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.script.Console.superclass.initComponent.apply(this, arguments);
  },
  
  switchEditorMode: function(){
    var typeName = this.getTypeNameFromMimetype(this.script.type);
    this.editorPanel.setMode(typeName);
  },
          
  getTypeNameFromMimetype: function(mimetype){
    var name = mimetype;
    var index = this.typeStore.findBy(function(r){
      var mt = r.get('mime-type');
      if (debug){
        console.debug('prepare console for mimetype: ' + mimetype);
      }
      var result = false;
      for ( var i=0; i<mt.length; i++ ){
        if (mt[i] === mimetype){
          result = true;
          break;
        }
      }
      return result;
    });
    
    if ( index >= 0 ){
      var r = this.typeStore.getAt(index);
      if (r){
        name = r.get('name');
      }
    } else if (debug){
      console.debug('could not find script type for ' + this.script.type);
    }
    return name;
  },
          
  loadScriptContent: function(){
    if (debug){
      console.debug('try to load script: ');
      console.debug(this.script);
    }
    
    var contentUrl = restUrl + 'plugins/script/content/' + this.script.id;
    
    Ext.Ajax.request({
      url: contentUrl,
      method: 'GET',
      scope: this,
      success: function(response){
        if ( debug ){
          console.debug('loaded script, from ' + contentUrl);
        }
        this.editorPanel.setValue(response.responseText);
      },
      failure: function(response){
        main.handleFailure(
          response.status, 
          this.errorTitleText, 
          this.errorArchiveMsgText
        );
      }
    });
  },
  
  getTypeCombobox: function(){
    if ( ! this.typeCombobox ){
      this.typeCombobox = Ext.getCmp('typeCombobox');
    }
    return this.typeCombobox;
  },
  
  changeScriptLanguage: function(combo, record){
    if (debug){
      console.debug('change script language to ' + record.get('name'));
    }
    this.editorPanel.setMode(record.get('name'));
  },
  
  execute: function(){
    var el = this.el;
    var tid = setTimeout( function(){el.mask('Loading ...');}, 100);
    var cmp = this.getTypeCombobox();
    var record = this.typeStore.getById( cmp.getValue() );
    
    Ext.Ajax.request({
      url: restUrl + 'plugins/script',
      method: 'POST',
      params: this.editorPanel.getValue(),
      scope: this,
      headers: {
        'Content-Type': record.get('mime-type')[0]
      },
      success: function(response){
        this.outputPanel.update('<pre>' + response.responseText + '</pre>');
        clearTimeout(tid);
        el.unmask();
      },
      failure: function(response){
        this.outputPanel.update('<pre>' + response.responseText + '</pre>');
        clearTimeout(tid);
        el.unmask();
        main.handleFailure(
          response.status, 
          this.errorTitleText, 
          this.errorArchiveMsgText
        );
      }
    });
  }
  
});

Ext.reg('console', Sonia.script.Console);