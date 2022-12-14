SPDXVersion: SPDX-1.2
DataLicense: <p xmlns="http://www.w3.org/1999/xhtml" style="margin-left: 20px;">Creative Commons Zero v1.0 Universal</p>^^http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral
DocumentComment: <text>This is a sample spreadsheet</text>

## Creation Information
Creator: Person: Gary O'Neall
Creator: Organization: Source Auditor Inc.
Creator: Tool: SourceAuditor-V1.2
Created: 2010-02-03T00:00:00Z
LicenseListVersion: 1.19
CreatorComment: <text>This is an example of an SPDX spreadsheet format</text>

## Review Information
Reviewer: Person: Suzanne Reviewer
ReviewDate: 2011-03-13T00:00:00Z
ReviewComment: <text>Another example reviewer.</text>

Reviewer: Person: Joe Reviewer
ReviewDate: 2010-02-10T00:00:00Z
ReviewComment: <text>This is just an example.  Some of the non-standard licenses look like they are actually BSD 3 clause licenses</text>

## Package Information
PackageName: SPDX Translator
PackageVersion: Version1.2.0
PackageHomePage: http://www.spdx.org/tools
PackageDownloadLocation: http://git.spdx.org/?p=spdx-tools.git;a=summary
PackageSummary: <text>SPDX Translator utility</text>
PackageSourceInfo: Version 1.0 of the SPDX Translator application
PackageFileName: spdxtranslator-1.2.zip
PackageSupplier: Organization:Linux Foundation
PackageOriginator: Organization:SPDX
PackageChecksum: SHA1: 2fd4e1c67a2d28fced849ee1bb76e7391b93eb12
PackageVerificationCode: c8ff32b6fe1200abadcdddda79d677f538c3cec3 (SpdxTranslatorSpdx.rdf, SpdxTranslatorSpdx.txt)
PackageDescription: <text>This utility translates and SPDX RDF XML document to a spreadsheet, translates a spreadsheet to an SPDX RDF XML document and translates an SPDX RDFa document to an SPDX RDF XML document.</text>

PackageCopyrightText: <text> Copyright 2010, 2011 Source Auditor Inc.</text>

PackageLicenseDeclared: (LicenseRef-4 AND LicenseRef-3 AND MPL-1.1 AND Apache-2.0 AND LicenseRef-2 AND LicenseRef-1)
PackageLicenseConcluded: (LicenseRef-4 AND LicenseRef-3 AND MPL-1.1 AND Apache-2.0 AND Apache-1.0 AND LicenseRef-2 AND LicenseRef-1)

PackageLicenseInfoFromFiles: LicenseRef-1
PackageLicenseInfoFromFiles: Apache-1.0
PackageLicenseInfoFromFiles: LicenseRef-2
PackageLicenseInfoFromFiles: LicenseRef-4
PackageLicenseInfoFromFiles: LicenseRef-3
PackageLicenseInfoFromFiles: Apache-2.0
PackageLicenseInfoFromFiles: MPL-1.1
PackageLicenseComments: <text>The declared license information can be found in the NOTICE file at the root of the archive file</text>

## File Information
FileName: lib-source/commons-lang3-3.1-sources.jar
FileType: ARCHIVE
FileChecksum: SHA1: c2b4e1c67a2d28fced849ee1bb76e7391b93f125
LicenseConcluded: Apache-2.0
LicenseInfoInFile: Apache-2.0
FileCopyrightText: <text>Copyright 2001-2011 The Apache Software Foundation</text>
ArtifactOfProjectName: Apache Commons Lang
ArtifactOfProjectHomePage: http://commons.apache.org/proper/commons-lang/
FileContributor: Apache Software Foundation
FileNotice: <text>Apache Commons Lang
Copyright 2001-2011 The Apache Software Foundation

This product includes software developed by
The Apache Software Foundation (http://www.apache.org/).

This product includes software from the Spring Framework,
under the Apache License 2.0 (see: StringUtils.containsWhitespace())</text>
FileComment: <text>This file is used by Jena</text>

FileName: lib-source/jena-2.6.3-sources.jar
FileType: ARCHIVE
FileChecksum: SHA1: 3ab4e1c67a2d28fced849ee1bb76e7391b93f125
LicenseConcluded: LicenseRef-1
LicenseInfoInFile: LicenseRef-1
LicenseComments: This license is used by Jena
FileCopyrightText: <text>(c) Copyright 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP</text>
ArtifactOfProjectName: Jena
ArtifactOfProjectHomePage: http://www.openjena.org/
FileDependency: lib-source/commons-lang3-3.1-sources.jar
FileContributor: Hewlett Packard Inc.
FileContributor: Apache Software Foundation
FileComment: <text>This file belongs to Jena</text>

FileName: src/org/spdx/parser/DOAPProject.java
FileType: SOURCE
FileChecksum: SHA1: 2fd4e1c67a2d28fced849ee1bb76e7391b93eb12
LicenseConcluded: Apache-2.0
LicenseInfoInFile: Apache-2.0
FileCopyrightText: <text>Copyright 2010, 2011 Source Auditor Inc.</text>
FileDependency: lib-source/jena-2.6.3-sources.jar
FileDependency: lib-source/commons-lang3-3.1-sources.jar
FileContributor: Open Logic Inc.
FileContributor: Black Duck Software In.c
FileContributor: Source Auditor Inc.
FileContributor: SPDX Technical Team Members
FileContributor: Protecode Inc.


## License Information
LicenseID: LicenseRef-1
ExtractedText: <text>/*
 * (c) Copyright 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */</text>

LicenseID: LicenseRef-3
ExtractedText: <text>The CyberNeko Software License, Version 1.0

 
(C) Copyright 2002-2005, Andy Clark.  All rights reserved.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer. 

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution,
   if any, must include the following acknowledgment:  
     "This product includes software developed by Andy Clark."
   Alternately, this acknowledgment may appear in the software itself,
   if and wherever such third-party acknowledgments normally appear.

4. The names "CyberNeko" and "NekoHTML" must not be used to endorse
   or promote products derived from this software without prior 
   written permission. For written permission, please contact 
   andyc@cyberneko.net.

5. Products derived from this software may not be called "CyberNeko",
   nor may "CyberNeko" appear in their name, without prior written
   permission of the author.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR OTHER CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</text>
LicenseName: CyberNeko License
LicenseCrossReference: http://people.apache.org/~andyc/neko/LICENSE, http://justasample.url.com
LicenseComment: <text>This is tye CyperNeko License</text>

LicenseID: LicenseRef-2
ExtractedText: <text>This package includes the GRDDL parser developed by Hewlett Packard under the following license:
?? Copyright 2007 Hewlett-Packard Development Company, LP

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission. 
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. </text>

LicenseID: LicenseRef-4
ExtractedText: <text>/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */  </text>

