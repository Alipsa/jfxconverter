function getFullTextContent() {
  var fullText = [  {"name": "Home","id": "_index_","meta": "The docJGenerator project allows to generate a html site with a list of XML files defining a documentation","url": "index.html"},
  {"name": "Access to external links","id": "access_to_external_links","meta": "Configure how to access external links","url": "articles/article_externallinks.html"},
  {"name": "Algorithm","id": "algorithm","meta": "This article presents the way the tool works","url": "articles/article_algorithm.html"},
  {"name": "Ant integration","id": "ant_integration","meta": "This article is about how to integrate the wiki generator in ant build files","url": "articles/article_ant.html"},
  {"name": "apidoc","id": "apidoc","meta": "This article explains how the apidoc element can be used","url": "articles/article_apidoc.html"},
  {"name": "APIs elements","id": "apis_elements","meta": "This article presents the elements which link to APIs or embed APIs in the wiki","url": "articles/article_apiselts.html"},
  {"name": "APIs property","id": "apis_property","meta": "Configure how to access external APIs and wikis","url": "articles/article_apis.html"},
  {"name": "Articles","id": "articles","meta": "Article files are XML files which define the articles in the wiki","url": "articles/article_articles.html"},
  {"name": "Basic usage","id": "basic_usage","meta": "This article presents the basic usage and configuration of the tool","url": "articles/article_basicusage.html"},
  {"name": "Categories","id": "categories","meta": "This article explains the concept of categories","url": "articles/article_categories.html"},
  {"name": "Chapters and titles","id": "chapters_and_titles","meta": "Chapters and titles allow to define sections in the articles (<Hi> html tag)","url": "articles/article_chapters.html"},
  {"name": "Character encoding","id": "character_encoding","meta": "This article explains what character encoding is used for the input or the output files","url": "articles/article_characterencoding.html"},
  {"name": "Checking external links","id": "checking_external_links","meta": "This article presents how the checking of external links is performed","url": "articles/article_checklinks.html"},
  {"name": "Command-line starting","id": "command-line_starting","meta": "This article is about how to execute the application by the command-line without showing the UI","url": "articles/article_commandline.html"},
  {"name": "Comparison with JavaHelp","id": "comparison_with_javahelp","meta": "This article compares the help feature of docJGenerator with the JavaHelp system","url": "articles/article_javahelp.html"},
  {"name": "Comparison with Markdown syntax","id": "comparison_with_markdown_syntax","meta": "This article compares the docJGenerator syntax to the Markdown syntax","url": "articles/article_markdowncomp.html"},
  {"name": "Comparison with other wikis softwares","id": "comparison_with_other_wikis_softwares","meta": "This article compares the docJGenerator features with other wikis softwares","url": "articles/article_comparisons.html"},
  {"name": "Comparison with wikipedia syntax","id": "comparison_with_wikipedia_syntax","meta": "This article compares the docJGenerator syntax to the wikipedia syntax","url": "articles/article_wikipedia.html"},
  {"name": "condition element","id": "condition_element","meta": "This article explains the XML tags which can be used to specify a condition","url": "articles/article_syntaxcondition.html"},
  {"name": "Configuration file","id": "configuration_file","meta": "It is possible to define an optional property / value configuration file when starting the application (using the graphical UI or the command line)","url": "articles/article_configFile.html"},
  {"name": "Context-sensitive Help","id": "context-sensitive_help","meta": "This article explains how to use the context-sensitive Help","url": "articles/article_ctxsensitivehelp.html"},
  {"name": "Context-sensitive Help JavaFX tutorial","id": "context-sensitive_help_javafx_tutorial","meta": "This article is tutorial which explains how to add a Context-sensitive Help to a JavaFX application","url": "articles/article_tutorialContextHelpJFX.html"},
  {"name": "Context-sensitive Help tutorial","id": "context-sensitive_help_tutorial","meta": "This article is tutorial which explains how to add a Context-sensitive Help to a Swing application","url": "articles/article_tutorialContextHelp.html"},
  {"name": "count property","id": "count_property","meta": "This article presents the count property which can be used in the wiki","url": "articles/article_countproperty.html"},
  {"name": "CSharp generic API","id": "csharp_generic_api","meta": "This article presents the CSharp generic API","url": "articles/article_apidoccsharp.html"},
  {"name": "Custom menus","id": "custom_menus","meta": "It is possible to add a custom content to the left menu, and also add a custom right menu to the index file","url": "articles/article_custommenus.html"},
  {"name": "Custom properties","id": "custom_properties","meta": "This article explains how to implements custom properties for Plugins","url": "articles/article_customproperties.html"},
  {"name": "Custom StyleSheet","id": "custom_stylesheet","meta": "The css property in the configuration file or the command-line allows to specify a custom StyleSheet for the HTML result","url": "articles/article_css.html"},
  {"name": "Customizing the elements style","id": "customizing_the_elements_style","meta": "This article explains the two ways to customize the elements style","url": "articles/article_customizingstyle.html"},
  {"name": "Customizing the wiki","id": "customizing_the_wiki","meta": "This article explains how to customize the wiki presentation","url": "articles/article_customize.html"},
  {"name": "Dependencies","id": "dependencies","meta": "This article is about the libraries dependencies of docJGenerator","url": "articles/article_dependencies.html"},
  {"name": "Dictionnary","id": "dictionnary","meta": "The wiki generator automatically generates a dictionnary of all the articles","url": "articles/article_dictionnary.html"},
  {"name": "Disambiguation articles","id": "disambiguation_articles","meta": "Disambiguation articles allow to specify an article which will point to several articles of approaching names","url": "articles/article_disambarticles.html"},
  {"name": "Distribution","id": "distribution","meta": "This article is about the distribution of the tool","url": "articles/article_distrib.html"},
  {"name": "DocGenerator Help feature","id": "docgenerator_help_feature","meta": "This article explains how to use the JavaHelp-like feature of the tool","url": "articles/article_docgenhelp.html"},
  {"name": "Document content specification","id": "document_content_specification","meta": "This article explains how to filter and configure the content of the wiki output","url": "articles/article_chapterscontent.html"},
  {"name": "Document generation tutorial","id": "document_generation_tutorial","meta": "This article is a tutorial which explains how to generate a PDF or DOCX document","url": "articles/article_tutorialdoc.html"},
  {"name": "DOCX custom CSS support","id": "docx_custom_css_support","meta": "This article explains the level of support for the custom CSS styles by the docx generation","url": "articles/article_docxcssupport.html"},
  {"name": "DOCX generation","id": "docx_generation","meta": "This article explains how to generate a Word file rather than an HTML site","url": "articles/article_docx.html"},
  {"name": "Doxygen generic API","id": "doxygen_generic_api","meta": "This article presents the Doxygen generic API","url": "articles/article_apidocdoxygen.html"},
  {"name": "ElementHook tree visit","id": "elementhook_tree_visit","meta": "This article explains how the ElementHook is called to modify the wiki source tree content","url": "articles/article_hooktreevisit.html"},
  {"name": "Elements declarations","id": "elements_declarations","meta": "This article explains how to handle element declarations in the Scripts","url": "articles/article_elementdecl.html"},
  {"name": "Elements to CSS selectors","id": "elements_to_css_selectors","meta": "This article explains the correspondance from elements to CSS selectors","url": "articles/article_cssSelectors.html"},
  {"name": "Escaping characters","id": "escaping_characters","meta": "This article explains how to escape characters in the articles content","url": "articles/article_escapingCharacters.html"},
  {"name": "Escaping non UTF8 characters","id": "escaping_non_utf8_characters","meta": "This article explains how to escape non UTF8 characters in the articles content","url": "articles/article_escapeNonUTF8.html"},
  {"name": "FAQ","id": "faq","meta": "Frequently Asked Questions","url": "articles/article_faq.html"},
  {"name": "First tutorial","id": "first_tutorial","meta": "This article is a tutorial to create your first wiki","url": "articles/article_tutorial1.html"},
  {"name": "Footer and header files","id": "footer_and_header_files","meta": "This article is about the Footer and Header specifications","url": "articles/article_footerAndHeader.html"},
  {"name": "Generic apidocs API styles","id": "generic_apidocs_api_styles","meta": "This article presents the generic styles used by the apidocs configuration","url": "articles/article_apidocstyles.html"},
  {"name": "Generic apidocs API types","id": "generic_apidocs_api_types","meta": "This article explains which elements are managed by each generic API type","url": "articles/article_genericapitypes.html"},
  {"name": "Glossary","id": "glossary","meta": "This article explains the glossary","url": "articles/article_glossary.html"},
  {"name": "GUI interface","id": "gui_interface","meta": "This article is about the GUI interface of the application","url": "articles/article_gui.html"},
  {"name": "Handling HTML entities","id": "handling_html_entities","meta": "This article explains how to handle HTML entities in articles content","url": "articles/article_handlingHTMLEntities.html"},
  {"name": "Help API overview","id": "help_api_overview","meta": "This article presents an overview of the Help API","url": "articles/article_helpapi.html"},
  {"name": "Help content configuration","id": "help_content_configuration","meta": "This article explains how to configure the help content","url": "articles/article_helpcontent.html"},
  {"name": "Help JavaFX API","id": "help_javafx_api","meta": "This article explains how to use the Help JavaFX API","url": "articles/article_helpjavafx.html"},
  {"name": "Help search","id": "help_search","meta": "This article is about the autocompletion field in the help GUI to search in articles","url": "articles/article_helpsearch.html"},
  {"name": "Help Swing API","id": "help_swing_api","meta": "This article explains how to use the Help Swing API","url": "articles/article_helpswing.html"},
  {"name": "Help system dependencies","id": "help_system_dependencies","meta": "This article explains the libraries dependencies of the help system","url": "articles/article_helpdependencies.html"},
  {"name": "Help system JavaFX tutorial","id": "help_system_javafx_tutorial","meta": "This article is tutorial which explains how to produce and use a Help content in a JavaFX application","url": "articles/article_tutorialJavaHelpJFX.html"},
  {"name": "Help system tutorial","id": "help_system_tutorial","meta": "This article is tutorial which explains how to produce and use a Help content in a Swing application","url": "articles/article_tutorialJavaHelp.html"},
  {"name": "Help usage","id": "help_usage","meta": "This article explains the usage of the Help component","url": "articles/article_helpusage.html"},
  {"name": "Image files","id": "image_files","meta": "These files define the images ids and their associated files","url": "articles/article_imageFiles.html"},
  {"name": "Importing JavaHelp content","id": "importing_javahelp_content","meta": "This article explains how to convert JavaHelp content to the docJGenerator Help content format","url": "articles/article_importjavahelp.html"},
  {"name": "Infobox template","id": "infobox_template","meta": "This article explains how to specify infobox templates","url": "articles/article_infobox.html"},
  {"name": "Internal API documentation configuration","id": "internal_api_documentation_configuration","meta": "This article explains how to specify internal APIs documentations languages, used in the apidoc element","url": "articles/article_apidocconf.html"},
  {"name": "Java generic API","id": "java_generic_api","meta": "This article presents the Java generic API","url": "articles/article_apidocjava.html"},
  {"name": "Javadocs APIs","id": "javadocs_apis","meta": "Configure how to access external Javadocs APIs","url": "articles/article_javadocs.html"},
  {"name": "Justification","id": "justification","meta": "This article explains how to justify articles content","url": "articles/article_justification.html"},
  {"name": "License","id": "license","meta": "License","url": "articles/article_license.html"},
  {"name": "Linking to local resources","id": "linking_to_local_resources","meta": "This article explains how to link to local resources (which can be images, files, or APIs)","url": "articles/article_linktoresources.html"},
  {"name": "List bullet style","id": "list_bullet_style","meta": "This article explains how to customize the bullet style of lists","url": "articles/article_listbullet.html"},
  {"name": "Localization","id": "localization","meta": "This article explains how to customize the localization of the wiki article pages","url": "articles/article_localization.html"},
  {"name": "Markdown markup","id": "markdown_markup","meta": "This article explains the supported Markdown markup","url": "articles/article_markdownmarkup.html"},
  {"name": "Markdown syntax","id": "markdown_syntax","meta": "This article explains the syntax which is supported by the tool","url": "articles/article_markdown.html"},
  {"name": "Mathematical formulas","id": "mathematical_formulas","meta": "This article is about the syntax for mathematical formulas","url": "articles/article_math.html"},
  {"name": "Mediawiki markup","id": "mediawiki_markup","meta": "This article explains the supported Mediawiki markup","url": "articles/article_mediawikimarkup.html"},
  {"name": "Mediawiki syntax","id": "mediawiki_syntax","meta": "This article explains the Mediawiki syntax which is supported by the tool","url": "articles/article_mediawiki.html"},
  {"name": "Mediawikis","id": "mediawikis","meta": "Configure how to access external Mediawikis","url": "articles/article_wikis.html"},
  {"name": "Naming constraints","id": "naming_constraints","meta": "This article is about the naming constraints of the articles","url": "articles/article_namingconstraints.html"},
  {"name": "OutputType configuration property","id": "outputtype_configuration_property","meta": "This article is about the outputType configuration property","url": "articles/article_outputtype.html"},
  {"name": "Packages dependencies","id": "packages_dependencies","meta": "This article explains how to enforce package dependencies","url": "articles/article_packagedepend.html"},
  {"name": "PDF and DOCX generation configuration","id": "pdf_and_docx_generation_configuration","meta": "This article explains how to configure the generation of the PDF or DOCX file","url": "articles/article_pdfdocxconfig.html"},
  {"name": "PDF custom CSS support","id": "pdf_custom_css_support","meta": "This article explains the level of support for the custom CSS styles by the PDF generation","url": "articles/article_pdfcssupport.html"},
  {"name": "PDF generation","id": "pdf_generation","meta": "This article explains how to generate a PDF file rather than an HTML site","url": "articles/article_pdf.html"},
  {"name": "Performance","id": "performance","meta": "This article explains how to speed-up the performance of the generator","url": "articles/article_performance.html"},
  {"name": "Plugin ElementHook","id": "plugin_elementhook","meta": "This article explains how to implements the ElementHook to modify the wiki source tree content before the generation","url": "articles/article_elementhook.html"},
  {"name": "Plugins framework","id": "plugins_framework","meta": "This article explains the Plugins framework","url": "articles/article_plugins.html"},
  {"name": "Properties","id": "properties","meta": "This article presents the general properties which can be used in the wiki","url": "articles/article_properties.html"},
  {"name": "Python generic API","id": "python_generic_api","meta": "This article presents the Python generic API","url": "articles/article_apidocpython.html"},
  {"name": "Qt APIs","id": "qt_apis","meta": "Configure how to access external Qt APIs","url": "articles/article_qtdocs.html"},
  {"name": "Redirect articles","id": "redirect_articles","meta": "Redirect articles allow to create an article with a redirection to another article or even a title or anchor in another article","url": "articles/article_redirectarticles.html"},
  {"name": "References","id": "references","meta": "This article explains the concept of inter-wiki links","url": "articles/article_references.html"},
  {"name": "Relaxed Syntax","id": "relaxed_syntax","meta": "This article is about the relaxed syntax which can be used on the articles","url": "articles/article_relaxedsyntax.html"},
  {"name": "Resource files","id": "resource_files","meta": "This article is about specifying and using resource files","url": "articles/article_resourceFiles.html"},
  {"name": "Root files and packages","id": "root_files_and_packages","meta": "This article explains how to use the generator with more than one root directory","url": "articles/article_roots.html"},
  {"name": "Scripts","id": "scripts","meta": "This article explains how to use the Scripting Plugin","url": "articles/article_scripts.html"},
  {"name": "Search box","id": "search_box","meta": "This article is about the Search box wich is shown on articles","url": "articles/article_search.html"},
  {"name": "Second tutorial","id": "second_tutorial","meta": "This article is a tutorial which explains the concepts of images and resources","url": "articles/article_tutorial2.html"},
  {"name": "Setting a background","id": "setting_a_background","meta": "The optional background image or color to use for the articles page","url": "articles/article_background.html"},
  {"name": "Syntax","id": "syntax","meta": "This article explains the XML tags which can be used to specify the articles syntax","url": "articles/article_syntax.html"},
  {"name": "Syntax end elements","id": "syntax_end_elements","meta": "This article explains the XML tags which can be used at the beginning of articles","url": "articles/article_syntaxEndElements.html"},
  {"name": "Syntax highlighting","id": "syntax_highlighting","meta": "This article explains how the syntax highlighting in the pre element works","url": "articles/article_syntaxhighlighting.html"},
  {"name": "Syntax justification elements","id": "syntax_justification_elements","meta": "This article explains the XML tags which can be used to specify the articles justification","url": "articles/article_syntaxJustifElements.html"},
  {"name": "Syntax overview","id": "syntax_overview","meta": "This article presents an overview of the tags supported in the XML syntax","url": "articles/article_syntaxoverview.html"},
  {"name": "Syntax starting elements","id": "syntax_starting_elements","meta": "This article explains the XML tags which can be used at the beginning of articles","url": "articles/article_syntaxStartingElements.html"},
  {"name": "table element","id": "table_element","meta": "This article explains the XML tags which can be used to specify tables","url": "articles/article_syntaxtable.html"},
  {"name": "Troubleshooting","id": "troubleshooting","meta": "Troubleshooting","url": "articles/article_troubleshooting.html"},
  {"name": "Tutorials","id": "tutorials","meta": "This article presents a list of tutorials","url": "articles/article_tutorials.html"},
  {"name": "Types of files","id": "types_of_files","meta": "This article explains the types of files which can be found in the wiki input","url": "articles/article_typesoffiles.html"},
  {"name": "Usage in a headless environment","id": "usage_in_a_headless_environment","meta": "This article is about how to execute the application in a headless environment","url": "articles/article_headless.html"},
  {"name": "Using the Help API with manually produced content","id": "using_the_help_api_with_manually_produced_content","meta": "This article explains how to produce the Help content manually","url": "articles/article_helpmanualcontent.html"},
  {"name": "Wiki source tree","id": "wiki_source_tree","meta": "This article presents the concept of the wiki source tree","url": "articles/article_sourcetree.html"},
  {"name": "Wiki status","id": "wiki_status","meta": "This article explains how to show the wiki status in the wiki","url": "articles/article_showstatus.html"}]
  return fullText;
}