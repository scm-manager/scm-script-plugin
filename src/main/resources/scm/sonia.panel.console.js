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

Ext.ns('Sonia.panel');

Sonia.panel.Console = Ext.extend(Ext.Panel, {
  
  editorPanel: null,
  outputPanel: null,
  typeCombobox: null,
  
  initComponent: function(){
    this.editorPanel = new Sonia.panel.CodeEditorPanel({
      region: 'center',
      layout: 'fit'
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
    
    var scriptTypeStore = new Sonia.rest.JsonStore({
      proxy: new Ext.data.HttpProxy({
        url: restUrl + 'plugins/script/supported-types.json',
        method: 'GET'
      }),
      fields: ['name', 'display-name', 'mime-type'],
      root: 'types',
      idProperty: 'name',
      autoLoad: true,
      autoDestroy: true
    });
    
    var scriptsStore = new Sonia.rest.JsonStore({
      proxy: new Ext.data.HttpProxy({
        url: restUrl + 'plugins/script/stored-scripts.json',
        method: 'GET'
      }),
      fields: ['id', 'name', 'description', 'type', 'content'],
      root: 'script',
      idProperty: 'id',
      autoLoad: false,
      autoDestroy: true
    });
    
    var config = {
      title: 'Console',
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
        store: scriptTypeStore,
        listeners: {
          select: {
            fn: this.changeScriptLanguage,
            scope: this
          }
        }
      },'-',{
        xtype: 'label',
        text: 'Samples: ',
        cls: 'ytb-text'
      },'  ',{
        id: 'storedScriptsCombobox',
        xtype: 'combo',
        name: 'stored-script',
        triggerAction: 'all',
        editable: false,
        displayField: 'name',
        valueField: 'id',
        store: scriptsStore,
        listeners: {
          select: {
            fn: this.changeStoredScript,
            scope: this
          }
        }
      }],
      items: [this.editorPanel, this.outputPanel]
    };
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.panel.Console.superclass.initComponent.apply(this, arguments);
  },
  
  getTypeCombobox: function(){
    if ( ! this.typeCombobox ){
      this.typeCombobox = Ext.getCmp('typeCombobox');
    }
    return this.typeCombobox;
  },
  
  changeStoredScript: function(combo, record){
    var cmp = this.getTypeCombobox();
    var type = record.get('type');
    var index = cmp.getStore().findBy(function(r){
      var mt = r.get('mime-type');
      var result = false;
      for ( var i=0; i<mt.length; i++ ){
        if (mt[i] == type){
          result = true;
          break;
        }
      }
      return result;
    });
    if ( index >= 0 ){
      var r = cmp.getStore().getAt(index);
      if (r){
        cmp.setValue(r.get('name'));
        this.changeScriptLanguage(cmp, r);
      }
    }
    this.editorPanel.setValue(record.get('content'));
  },
  
  changeScriptLanguage: function(combo, record){
    if (debug){
      console.debug('change script language to ' + record.get('name'));
    }
    this.editorPanel.setMode('ace/mode/' + record.get('name').toLowerCase());
  },
  
  execute: function(){
    var el = this.el;
    var tid = setTimeout( function(){el.mask('Loading ...');}, 100);
    
    var cmp = this.getTypeCombobox();
    var record = cmp.getStore().getById( cmp.getValue() );
    
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

Ext.reg('console', Sonia.panel.Console);